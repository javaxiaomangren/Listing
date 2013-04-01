DROP TABLE IF EXISTS _shop_count;

DROP TABLE IF EXISTS _shop_categories;

DROP TABLE IF EXISTS $TMP_TABLE;

-- DROP PROCEDURE IF EXISTS extract_by_categories;

CREATE TABLE $TMP_TABLE (
  city_code   CHAR(6)      NOT NULL, -- 城市id
  bank_id     INT          NOT NULL, -- 银行id
  type_code   CHAR(1)      NOT NULL, -- 类型编码（'B' - 品牌, 'S' - 商户）
  id          INT          NOT NULL, -- 品牌id/商户id
  category    VARCHAR(255) NULL,     -- 行业类别编码
  create_time BIGINT       NOT NULL, -- 品牌/商户的创建时间
  deadline    BIGINT       NULL,     -- deal的截止日期（取最晚的一个）
  deal_id     VARCHAR(500) NULL,     -- deal id，以deal创建时间降序排列，多个以逗号分隔
  PRIMARY KEY (city_code, bank_id, type_code, id)
);

CREATE TEMPORARY TABLE _shop_count
SELECT s.city AS city
     , d.id   AS deal_id
     , COUNT(DISTINCT s.id) AS shop_count
  FROM deal        d
  JOIN deal_card   dc ON d.id = dc.deal_id
  JOIN card        c  ON dc.card_id = c.id
  JOIN deal_shop   ds ON d.id = ds.deal_id
  JOIN shop        s  ON ds.shop_id = s.id
 WHERE d.valid_flag = 1
   AND s.valid_flag = 1
 GROUP BY s.city, d.id
;

CREATE INDEX city ON _shop_count (city);

CREATE TEMPORARY TABLE _shop_categories (
  shop_id       INT      NOT NULL,
  category_id   CHAR(8),
  category_code CHAR(8)  NOT NULL,
  PRIMARY KEY (shop_id, category_code)
) DEFAULT CHARACTER SET = UTF8;

-- DELIMITER //
-- 
-- CREATE PROCEDURE extract_by_categories()
-- BEGIN
--   DECLARE done INT DEFAULT FALSE;
--   DECLARE a, b INT DEFAULT 1;
--   DECLARE id INT;
--   DECLARE category CHAR(8);
--   DECLARE subcates CHAR(255);
--   DECLARE shops CURSOR FOR
--    SELECT DISTINCT s.id                 AS shop_id
--         , TRIM(s.big_category) AS category
--         , TRIM(s.category)     AS subcates
--      FROM deal        d
--         , deal_card   dc
--         , card        c
--         , deal_shop   ds
--         , shop        s
--      WHERE d.id = dc.deal_id
--        AND c.id = dc.card_id
--        AND d.id = ds.deal_id
--        AND s.id = ds.shop_id
--        AND d.valid_flag = 1
--        AND s.valid_flag = 1;
--   DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
-- 
--   OPEN shops;
-- 
--   read_loop: LOOP
--      FETCH shops INTO id, category, subcates;
--      IF done THEN
--        LEAVE read_loop;
--      END IF;
--      IF subcates IS NOT NULL AND LENGTH(subcates) > 0 THEN
--        SET a = 1, b = 1;
--        inner_loop: LOOP
--          SET b = LOCATE(',', subcates, a);
--          IF b = 0 THEN
--            SET b = LENGTH(subcates) + 1;
--          END IF;
--          IF b > a THEN
--            INSERT INTO _shop_categories (shop_id, category_code) VALUES (id, SUBSTR(subcates, a, b - a));
--            IF b > LENGTH(subcates) THEN
--              LEAVE inner_loop;
--            END IF;
--            SET a = b + 1;
--          END IF;
--        END LOOP;
--      ELSEIF category IS NOT NULL AND LENGTH(category) > 0 THEN
--        INSERT INTO _shop_categories (shop_id, category_code) VALUES (id, category);
--      END IF;
--   END LOOP;
-- 
--   CLOSE shops;
-- 
-- END;
-- 
-- //
-- 
-- DELIMITER ;

CALL extract_by_categories();

UPDATE _shop_categories sc
     , category_code    c
   SET sc.category_id = c.id
 WHERE sc.category_code = c.code
;

-- 城市，商户，银行 -> 活动
CREATE TEMPORARY TABLE _temp1
SELECT s.city        AS city
     , s.id          AS shop_id
     , s.brand_id    AS brand_id
     , sb.brand_rank AS brand_rank
     , c.provider_id AS bank_id
     , d.id          AS deal_id
     , d.name        AS deal_name  
     , d.end_date    AS end_date
     , sc.shop_count AS shop_count
  FROM shop        s
  JOIN deal_shop   ds ON ds.shop_id = s.id
  JOIN deal        d  ON d.id = ds.deal_id
  JOIN deal_card   dc ON dc.deal_id = d.id
  JOIN card        c  ON c.id = dc.card_id
  JOIN _shop_count sc ON sc.city = s.city AND sc.deal_id = d.id
  LEFT JOIN store_brand sb ON sb.id = s.brand_id AND sb.valid_flag = 1
 WHERE s.valid_flag = 1
   AND ds.valid_flag = 1
   AND d.valid_flag = 1
   AND dc.valid_flag = 1
   AND c.valid_flag = 1
;

-- 城市，品牌，银行，活动名 -> 活动截止日期
CREATE TEMPORARY TABLE _temp2
SELECT city      AS city
     , brand_id  AS brand_id
     , bank_id   AS bank_id
     , deal_name AS deal_name
     , MAX(end_date) AS end_date
  FROM _temp1
 WHERE brand_rank IN (5, 6)
 GROUP BY city, brand_id, bank_id, deal_name
;

ALTER TABLE _temp2
  ADD PRIMARY KEY (city, brand_id, bank_id, deal_name, end_date),
  ADD COLUMN deal_id INT NULL,
  ADD COLUMN shop_count INT NULL;

UPDATE _temp2 dn
     , _temp1 d
   SET dn.deal_id = d.deal_id
     , dn.shop_count = d.shop_count
 WHERE dn.city = d.city
   AND dn.brand_id = d.brand_id
   AND dn.bank_id = d.bank_id
   AND dn.deal_name = d.deal_name
   AND dn.end_date = d.end_date
;

-- 城市，品牌，银行 -> 活动列表
CREATE TEMPORARY TABLE _temp3
SELECT city
     , brand_id
     , bank_id
     , GROUP_CONCAT(deal_id, ',', shop_count SEPARATOR ',') AS deal_id
  FROM _temp2
 GROUP BY city, brand_id, bank_id
;

-- 商户，银行 -> 活动列表
CREATE TEMPORARY TABLE _temp4
SELECT city      AS city
     , shop_id   AS shop_id
     , bank_id   AS bank_id
     , GROUP_CONCAT(DISTINCT deal_id, ',', shop_count ORDER BY end_date DESC SEPARATOR ',') AS deal_id
  FROM _temp1
 WHERE (brand_rank NOT IN (5, 6) OR brand_rank IS NULL)
 GROUP BY shop_id, bank_id
;

CREATE INDEX city_shop_bank ON _temp4 (city, shop_id, bank_id);

-- 导出特约品牌
INSERT INTO $TMP_TABLE
SELECT s.city
     , c.provider_id
     , 'B'
     , sb.id
     , GROUP_CONCAT(DISTINCT sc.category_id SEPARATOR ',')
     , COALESCE(UNIX_TIMESTAMP(sb.create_time) * 1000, 0)
     , MAX(UNIX_TIMESTAMP(d.end_date) * 1000)
     , NULL
  FROM _shop_categories sc
     , deal             d
     , deal_card        dc
     , card             c
     , deal_shop        ds
     , shop             s
     , store_brand      sb
 WHERE s.id = sc.shop_id
   AND d.id = dc.deal_id
   AND c.id = dc.card_id
   AND d.id = ds.deal_id
   AND s.id = ds.shop_id
   AND sb.id = s.brand_id
   AND s.valid_flag = 1
   AND d.valid_flag = 1
   AND ds.valid_flag = 1
   AND sb.valid_flag = 1
   AND sb.brand_rank IN (5, 6)
 GROUP BY sb.id, s.city, c.provider_id
;

UPDATE $TMP_TABLE t
     , _temp3     m
   SET t.deal_id = m.deal_id
 WHERE t.city_code = m.city
   AND t.bank_id = m.bank_id
   AND t.id = m.brand_id
   AND t.type_code = 'B'
;

-- 导出特约商户
INSERT INTO $TMP_TABLE
SELECT s.city
     , c.provider_id
     , 'S'
     , s.id
     , GROUP_CONCAT(DISTINCT sc.category_id SEPARATOR ',')
     , COALESCE(UNIX_TIMESTAMP(s.create_time) * 1000, 0)
     , MAX(UNIX_TIMESTAMP(d.end_date) * 1000)
     , NULL
  FROM _shop_categories sc
  JOIN shop             s  ON s.id = sc.shop_id
  JOIN deal_shop        ds ON ds.shop_id = s.id
  JOIN deal             d  ON d.id = ds.deal_id
  JOIN deal_card        dc ON dc.deal_id = d.id
  JOIN card             c  ON c.id = dc.card_id
  LEFT JOIN store_brand sb ON s.brand_id = sb.id AND sb.valid_flag = 1
 WHERE (sb.brand_rank NOT IN (5, 6) OR sb.id IS NULL)
   AND s.valid_flag = 1
   AND d.valid_flag = 1
   AND ds.valid_flag = 1
   AND dc.valid_flag = 1
   AND c.valid_flag = 1
   AND c.provider_id = 1
 GROUP BY s.id, c.provider_id
;

UPDATE $TMP_TABLE t
     , _temp4     m
   SET t.deal_id = m.deal_id
 WHERE t.city_code = m.city
   AND t.bank_id = m.bank_id
   AND t.id = m.shop_id
   AND t.type_code = 'S'
;

UPDATE $TMP_TABLE
   SET deadline = NULL
 WHERE FROM_UNIXTIME(deadline / 1000) > '2100-01-01 00:00:00.0'
;

CREATE INDEX cty_bnk_typ_ctime ON $TMP_TABLE (city_code, bank_id, type_code ASC, create_time DESC);
