DROP TABLE IF EXISTS $TMP_TABLE;

CREATE TABLE $TMP_TABLE (
  id          INT     NOT NULL,
  bank_id     INT     NOT NULL,
  type_code   CHAR(4) NOT NULL,
  create_time BIGINT  NOT NULL,
  city_code   CHAR(6) NOT NULL,
  deadline    BIGINT  NULL
);

INSERT INTO $TMP_TABLE (id, bank_id, city_code, type_code, create_time, deadline)
SELECT DISTINCT d.id AS id
     , c.provider_id AS bank_id
     , d.city        AS city_code
     , MIN(dtc.id)   AS type_code
     , unix_timestamp(d.create_time) * 1000 AS create_time
     , CASE WHEN d.end_date > '2100-01-01 00:00:00.0' THEN NULL
            ELSE unix_timestamp(d.end_date) * 1000
       END AS deadline
  FROM deal           d
     , deal_card      dc
     , card           c
     , deal_type_code dtc
 WHERE d.id = dc.deal_id
   AND c.id = dc.card_id
   AND dtc.code = d.deal_type_code
   AND deal_type_code <> 30000003
   AND d.valid_flag = 1
   AND dc.valid_flag = 1
   AND c.valid_flag = 1
 GROUP BY d.id, c.provider_id, d.city
;

ALTER TABLE $TMP_TABLE ADD PRIMARY KEY (bank_id, id);

CREATE INDEX bnk_cty_typ_ctime ON $TMP_TABLE (bank_id, city_code, type_code, create_time DESC);
