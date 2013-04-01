#!/bin/bash

cd $(dirname $0)

. $CONF

MYSQL_SRC="$MYSQL -C -h$SRC_HOST -P$SRC_PORT -u$SRC_USER --password=$SRC_PASSWORD --database=$SRC_DB"
MYSQL_DST="$MYSQL -C -h$DST_HOST -P$DST_PORT -u$DST_USER --password=$DST_PASSWORD --database=$DST_DB"
MYSQLDUMP_SRC="$MYSQLDUMP -C -h$SRC_HOST -P$SRC_PORT -u$SRC_USER --password=$SRC_PASSWORD --skip-comments"

TMP=$(mktemp)

echo Selecting deals into temp table ...
sed "s/\\\$TMP_TABLE/$DEAL_TMP_TABLE/" deal-export.sql > $TMP
sed "s/\\\$TMP_TABLE/$SHOP_TMP_TABLE/" shop-export.sql >> $TMP
$MYSQL_SRC < $TMP

echo Transfering temp table from source to destination ...
$MYSQLDUMP_SRC $SRC_DB $DEAL_TMP_TABLE $SHOP_TMP_TABLE | $MYSQL_DST

echo Droping temp table in source database ...
sed "s/\\\$TMP_TABLE/$DEAL_TMP_TABLE/" deal-post-export.sql > $TMP
sed "s/\\\$TMP_TABLE/$SHOP_TMP_TABLE/" shop-post-export.sql >> $TMP
$MYSQL_SRC < $TMP

echo Renaming temp table in destination database ...
sed "s/\\\$TMP_TABLE/$DEAL_TMP_TABLE/" deal-post-import.sql > $TMP
sed "s/\\\$TMP_TABLE/$SHOP_TMP_TABLE/" shop-post-import.sql >> $TMP
$MYSQL_DST < $TMP

rm $TMP
