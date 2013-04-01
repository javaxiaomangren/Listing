package com.hui800.listing.db.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.twitter.ostrich.stats.Stats
import org.squeryl.PrimitiveTypeMode._
import com.hui800.listing.util.SquerylUtils._
import com.hui800.listing.util.CollectionUtils._
import org.squeryl.dsl.ast.TypedExpressionNode

class DealQueriesImpl(db: DB) extends DealQueries {

  import db._
  
  def now = System.currentTimeMillis
  
  def getDealIds(params: QueryParams, sortKey: String) = Stats.time("getDealIds") {
    import params._
    val query = join(
      deals,
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty),
      dealShops.leftOuter,
      shops.leftOuter,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && brandTag.isEmpty),
      brandTags.inhibitWhen(brandTag.isEmpty)
    ) {
      (d, dc, ds, s, db, bt) =>
      val sortByTime = (sortKey == "time")
      where(
        // 排除过期deal
        (d.timestamp gt now) and
        (d.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        d.dealType === dealType.? and
        (d.subType in subType).inhibitWhen(subType.isEmpty) and
        bitAnd(d.opTag, opTag.?) === opTag.? and
        dc.get.categoryId === categoryId.? and
        dc.get.subCateId === subCateId.? and
        ((s.get.id.isNull and (d.cityId === 0 or d.cityId === cityId)) or 
         (s.get.cityId === cityId and
          s.get.districtId === districtId.? and
          s.get.bizoneId === bizoneId.?)) and
        (s.get.id === shopId.?) and
        s.get.mallId === mallId.? and
        s.get.brandId === shopBrandId.? and
        (db.get.brandId === dealBrandId.?) and
        bt.get.tagId === brandTag.?
      )
      .select(d.id)
      .orderBy(
        d.isMallDeal desc,
        if (sortByTime) d.timestamp desc else d.typeOrder desc,
        if (sortByTime) d.typeOrder desc else d.timestamp desc
      )
      .on(
        dc.get.dealId === d.id,
        ds.get.dealId === d.id,
        s.get.id === ds.get.shopId,
        db.get.dealId === d.id,
        bt.get.brandId === db.get.brandId
      )
    }.distinct
    params.pager match {
      case Some(pager) => query.page(pager.skip, pager.size).toList
      case None => query.toList
    }
  }

  def getDealAndBankIds(params: QueryParams, sortKey: String) = Stats.time("getDealAndBankIds")  {
    import params._
    val query = join(
      deals,
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty),
      dealShops.leftOuter,
      shops.leftOuter,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && brandTag.isEmpty),
      brandTags.inhibitWhen(brandTag.isEmpty)
    ) {
      (d, dc, ds, s, db, bt) =>
      val orderByTime = (sortKey == "time")
      where(
        // 排除过期deal
        (d.timestamp gt now) and
        (d.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        d.dealType === dealType.? and
        (d.subType in subType).inhibitWhen(subType.isEmpty) and
        d.subType <> "30000003" and
        bitAnd(d.opTag, opTag.?) === opTag.? and
        dc.get.categoryId === categoryId.? and
        dc.get.subCateId === subCateId.? and
        (d.cityId === 0 or d.cityId === cityId or
         s.get.cityId === cityId) and
        (s.get.mallId === mallId.?) and
        s.get.brandId === shopBrandId.? and
        (db.get.brandId === dealBrandId.?) and
        bt.get.tagId === brandTag.? and
        d.bankId <> 0
      )
      .select(d.id, d.bankId)
      .orderBy(
        if (orderByTime) d.timestamp desc else d.typeOrder desc,
        if (orderByTime) d.typeOrder desc else d.timestamp desc
      )
      .on(
        dc.get.dealId === d.id,
        ds.get.dealId === d.id,
        s.get.id === ds.get.shopId,
        db.get.dealId === d.id,
        bt.get.brandId === db.get.brandId
      )
    }.distinct
    params.pager match {
      case Some(pager) => query.page(pager.skip, pager.size).toList
      case None => query.toList
    }
  }

  def countDealsGroupByCategory(params: QueryParams) = {
    countDeals[String](params, (dc, d, ds, s, db) => dc.get.categoryId, groupByCate = true)
  }

  def countDealsGroupBySubCate(params: QueryParams) = {
    countDeals[String](params, (dc, d, ds, s, db) => dc.get.subCateId, groupByCate = true)
  }

  def countDealsGroupByDistrict(params: QueryParams) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.map(_.districtId)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsGroupByBizone(params: QueryParams) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.map(_.bizoneId)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsGroupByDealType(params: QueryParams) = {
    countDeals[String](params, (dc, d, ds, s, db) => d.dealType)
  }

  def countDealsGroupBySubType(params: QueryParams) = {
    countDeals[String](params, (dc, d, ds, s, db) => d.subType)
  }

  def countDealsGroupByBank(params: QueryParams) = {
    countDeals[Int](params, (dc, d, ds, s, db) => d.bankId)
  }

  def countDealsGroupByMall(params: QueryParams) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.flatMap(_.mallId)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsGroupByBrand(params: QueryParams) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.flatMap(_.brandId), groupByBrand = true
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsGroupByShop(params: QueryParams) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.map(_.id)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsInMall(params: QueryParams, mallIds: Int*) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.flatMap(_.mallId), mallIds = mallIds.toList
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsInDealBrand(params: QueryParams, dealBrandIds: Int*) = {
    countDeals[Int](
      params, (dc, d, ds, s, db) => db.get.brandId, dealBrandIds = dealBrandIds.toList
    )
  }

  def countDealsInShopBrand(params: QueryParams, shopBrandIds: Int*) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.flatMap(_.brandId), shopBrandIds = shopBrandIds.toList
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDealsInShop(params: QueryParams, shopIds: Int*) = {
    countDeals[Option[Int]](
      params, (dc, d, ds, s, db) => s.map(_.id), shopIds = shopIds.toList
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countDeals(params: QueryParams) = Stats.time("countDeals")  {
    import params._
    join(
      deals,
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty),
      dealShops.leftOuter,
      shops.leftOuter,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && brandTag.isEmpty),
      brandTags.inhibitWhen(brandTag.isEmpty)
    ) {
      (d, dc, ds, s, db, bt) =>
      where(
        (d.timestamp gt now) and
        (d.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        d.dealType === dealType.? and
        (d.subType in subType).inhibitWhen(subType.isEmpty) and
        bitAnd(d.opTag, opTag.?) === opTag.? and
        dc.get.categoryId === categoryId.? and
        dc.get.subCateId === subCateId.? and
        ((s.get.id.isNull and (d.cityId === 0 or d.cityId === cityId)) or 
         (s.get.cityId === cityId and
          s.get.districtId === districtId.? and
          s.get.bizoneId === bizoneId.?)) and
        (s.get.mallId === mallId.?) and
        s.get.brandId === shopBrandId.? and
        (db.get.brandId === dealBrandId.?) and
        bt.get.tagId === brandTag.?
      )
      .compute(countDistinct(d.id))
      .on(
        dc.get.dealId === d.id,
        ds.get.dealId === d.id,
        s.get.id === ds.get.shopId,
        db.get.dealId === d.id,
        bt.get.brandId === db.get.brandId
      )
    }.single.measures
  }

  private def countDeals[K](
    params: QueryParams,
    groupKey: (Option[DealCategory], Deal, Option[DealShop], Option[Shop], Option[DealBrand]) => TypedExpressionNode[K],
    mallIds: List[Int] = Nil,
    dealBrandIds: List[Int] = Nil,
    shopBrandIds: List[Int] = Nil,
    shopIds: List[Int] = Nil,
    groupByCate: Boolean = false,
    groupByBrand: Boolean = false
  ): List[(K, Long)] = Stats.time("countDealsBy") {
    import params._
//    LoggerFactory.getLogger(this.getClass).info("{}{}", dealType, subType)
    val includeDealsWithNoShop = (dealType == None || dealType == Some("30000000") && subType != Some("30000003"))
    join(
      deals,
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty && !groupByCate),
      dealShops.leftOuter,
      shops.leftOuter,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && dealBrandIds.isEmpty && brandTag.isEmpty && !groupByBrand),
      brandTags.inhibitWhen(brandTag.isEmpty),
      shopCategories.inhibitWhen((categoryId.isEmpty && subCateId.isEmpty) || !groupByBrand)
    ) {
      (d, dc, ds, s, db, bt, sc) =>
      val key = groupKey(dc, d, ds, s, db)
      where(
        // 排除过期deal
        (d.timestamp gt now) and
        // 指定银行
        (d.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        // 指定类型
        d.dealType === dealType.? and
        (d.subType in subType).inhibitWhen(subType.isEmpty) and
        // 指定tag
        bitAnd(d.opTag, opTag.?) === opTag.? and
        // 指定城市
        ((s.get.id.isNull and (d.cityId === 0 or d.cityId === cityId)) or 
         (s.get.cityId === cityId and
          s.get.districtId === districtId.? and
          s.get.bizoneId === bizoneId.?)) and
        // 开卡、分期和积分优惠不随地域、行业变化
        dc.get.categoryId === categoryId.? and
        dc.get.subCateId === subCateId.? and
        (sc.get.categoryId === categoryId.? and
         sc.get.subCateId === subCateId.?).inhibitWhen(!groupByBrand) and
        s.get.mallId === mallId.? and
        s.get.brandId === shopBrandId.? and
        (s.get.brandId in shopBrandIds).inhibitWhen(shopBrandIds.isEmpty) and
        (s.get.id in shopIds).inhibitWhen(shopIds.isEmpty) and
        (s.get.mallId in mallIds).inhibitWhen(mallIds.isEmpty) and
        (db.get.brandId === dealBrandId.?) and
        (db.get.brandId in dealBrandIds).inhibitWhen(dealBrandIds.isEmpty) and
        bt.get.tagId === brandTag.?
      )
      .groupBy(key)
      .compute(key, countDistinct(d.id))
      .on(
        dc.get.dealId === d.id,
        ds.get.dealId === d.id,
        s.get.id === ds.get.shopId,
        db.get.dealId === d.id,
        bt.get.brandId === db.get.brandId,
        ds.get.shopId === sc.get.shopId
      )
    }.toList.map(m => (m.measures._1, m.measures._2))
  }

  def getDeals(dealIds: Int*) = Stats.time("getDeals") {
    val dealMap = deals.where(_.id in dealIds).map(m => (m.id, m)).toMap
    mapToList(dealMap, dealIds: _*)
  }

  def getSubTypeAndBankIds(dealIds: Int*) = Stats.time("getSubTypeAndBankIds") {
    from(deals)(d => where(d.id in dealIds and d.subType <> "30000003") select(d.id, d.subType, d.bankId)).toList
  }
}
