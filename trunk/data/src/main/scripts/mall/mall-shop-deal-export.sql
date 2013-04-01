DROP TABLE IF EXISTS $TMP_TABLE;

CREATE TABLE $TMP_TABLE (
  shop_id      INT          NOT NULL,
  deal_id      INT          NOT NULL,
  mall_deal_id INT          NULL,
  from_time    BIGINT       NOT NULL, -- 开始时间
  to_time      BIGINT       NOT NULL, -- 截止时间
  priority     INT          NOT NULL, -- 优先级（1 - 即将开始; 2 - 正在进行）
  create_time  BIGINT       NOT NULL, -- deal录入时间
  mall_id      INT          NOT NULL,
  floor_id     INT          NOT NULL,
  floor_order  INT          NOT NULL,
  subcates     VARCHAR(255) NULL,
  brand_id     INT          NOT NULL,
  brand_rank   INT          NOT NULL,
  py_name      VARCHAR(255) NOT NULL,
  PRIMARY KEY (deal_id, shop_id, priority, create_time DESC)
);

INSERT IGNORE INTO $TMP_TABLE
SELECT s.id
     , d.id
     , null
     , COALESCE(UNIX_TIMESTAMP(d.start_date) * 1000, 0)
     , UNIX_TIMESTAMP(d.end_date) * 1000
     , 2
     , UNIX_TIMESTAMP(d.create_time) * 1000
     , mb.mall_id
     , mb.floor_id
     , mb.floor_order
     , s.category
     , mb.brand_id
     , sb.brand_rank
     , LOWER(COALESCE(sb.py_name, sb.english_name))
  FROM shop        s
     , mall_brand  mb
     , store_brand sb 
     , deal_shop   ds 
     , deal        d
 WHERE s.id = mb.shop_id
   AND s.brand_id = sb.id
   AND s.id = ds.shop_id
   AND ds.deal_id = d.id
   AND d.valid_flag = 1
   AND s.valid_flag = 1
   AND mb.valid_flag = 1
   AND sb.valid_flag = 1
   AND ds.valid_flag = 1
   AND d.deal_belong = 3
;

-- 为每个商户创建一条虚拟deal，以便在商户没有任何优惠的情形下仍能被列出
INSERT IGNORE INTO $TMP_TABLE
SELECT s.id
     , 0
     , null
     , 0
     , 0
     , 3
     , 0
     , mb.mall_id
     , mb.floor_id
     , mb.floor_order
     , s.category
     , mb.brand_id
     , sb.brand_rank
     , LOWER(COALESCE(sb.py_name, sb.english_name))
  FROM shop        s
     , mall_brand  mb
     , store_brand sb
 WHERE s.id = mb.shop_id
   AND s.brand_id = sb.id
   AND s.valid_flag = 1
   AND mb.valid_flag = 1
   AND sb.valid_flag = 1
;

-- 即将开始的活动
INSERT IGNORE INTO $TMP_TABLE
SELECT ds.shop_id
     , ds.deal_id
     , null
     , COALESCE(UNIX_TIMESTAMP(start_date) * 1000, 0) - 2 * 86400000
     , UNIX_TIMESTAMP(start_date) * 1000
     , 1
     , UNIX_TIMESTAMP(d.create_time) * 1000
     , mb.mall_id
     , mb.floor_id
     , mb.floor_order
     , s.category
     , mb.brand_id
     , sb.brand_rank
     , LOWER(COALESCE(sb.py_name, sb.english_name))
FROM shop       s
   , deal_shop  ds
   , deal       d
   , mall_brand mb
   , store_brand sb
 WHERE s.id = ds.shop_id
   AND ds.deal_id = d.id
   AND s.id = mb.shop_id
   AND s.brand_id = mb.brand_id
   AND s.brand_id = sb.id
   AND s.valid_flag = 1
   AND ds.valid_flag = 1
   AND d.valid_flag = 1
   AND start_date > now()
   AND mb.valid_flag = 1
   AND sb.valid_flag = 1
   AND d.deal_belong = 3
;

UPDATE $TMP_TABLE tt, deal_deal dd
   SET tt.mall_deal_id = dd.b_deal_id
 WHERE tt.deal_id = dd.s_deal_id
;

