#!/bin/bash

cd $(dirname $0)

. $CONF

MYSQL_SRC="$MYSQL -C -h$SRC_HOST -P$SRC_PORT -u$SRC_USER --password=$SRC_PASSWORD --database=$SRC_DB --default-character-set=utf8"

#if [ ! -z '$DST_USER' ]; then
#  MONGO_OPTIONS="-u $DST_USER -p $DST_PASSWORD"
#fi

echo $MONGO_OPTIONS

TMP=$(mktemp)

$MYSQL_SRC --skip-column-names -e '
SELECT id
     , name
  FROM store_brand
 WHERE valid_flag = 1
' > $TMP
$MONGOIMPORT --host $DST_HOST --port $DST_PORT --db $DST_DB $MONGO_OPTIONS --drop --type tsv -c tmp_brands -f _id,name,version $TMP

$MYSQL_SRC --skip-column-names -e '
SELECT DISTINCT d.id
     , ds.shop_id
  FROM deal       d
     , deal_shop  ds
     , shop       s
 WHERE  d.valid_flag = 1
   AND ds.valid_flag = 1
   AND s.valid_flag = 1
   AND d.id = ds.deal_id
   AND ds.shop_id = s.id
   AND s.shop_type = 100001
   AND deal_belong = 1
' > $TMP
$MONGOIMPORT --host $DST_HOST --port $DST_PORT --db $DST_DB $MONGO_OPTIONS --drop --type tsv -c tmp_mall_deals -f _id,mall_id,version $TMP

# s_deal_id stands for small_deal_id or brand_deal_id rather than shop_deal_id
# b_deal_id stands for big_deal_id or mall_deal_id rather than brand_deal_id
$MYSQL_SRC --skip-column-names -e "
SELECT dd.s_deal_id
     , dd.b_deal_id
     , dd.brand_id
     , mb.mall_id
     , dd.title
     , dd.description
     , coalesce(dd.image_name, '')
     , floor_id
     , floor_order
     , floor_name
  FROM deal_deal  dd
     , deal       d
     , deal_shop  ds
     , mall_brand mb
 WHERE dd.b_deal_id = d.id
   AND dd.brand_id = mb.brand_id
   AND ds.shop_id = mb.mall_id
   AND ds.deal_id = d.id
   AND ds.valid_flag = 1
   AND d.valid_flag = 1
   AND mb.valid_flag = 1
" > $TMP
$MONGOIMPORT --host $DST_HOST --port $DST_PORT --db $DST_DB $MONGO_OPTIONS --drop --type tsv -c tmp_subdeals -f _id,deal_id,brand_id,mall_id,title,description,image,floor_id,floor_order,floor_name $TMP

echo "ver = $VERSION" > _process.js
cat process.js >> _process.js

$MONGO $DST_HOST:$DST_PORT/$DST_DB $MONGO_OPTIONS _process.js

rm _process.js
rm $TMP
