DROP TABLE IF EXISTS mall_shop_deal;

RENAME TABLE $TMP_TABLE TO mall_shop_deal;

CREATE INDEX mal_shp_pri_ctime ON mall_shop_deal (mall_id, shop_id, priority, create_time DESC);

CREATE INDEX mal_brd_pri_ctime ON mall_shop_deal (mall_id, brand_id, priority, create_time DESC);

CREATE INDEX mdl_shp_pri_ctime ON mall_shop_deal (mall_deal_id, shop_id, priority, create_time DESC);
