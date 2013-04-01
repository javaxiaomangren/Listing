package com.hui800.listing.db.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.twitter.ostrich.stats.Stats
import org.squeryl.PrimitiveTypeMode._
import com.hui800.listing.util.SquerylUtils._
import org.squeryl.dsl.ast.ExpressionNode
import org.squeryl.dsl.ast.TypedExpressionNode

class ShopQueriesImpl(db: DB) extends ShopQueries {
  
  import db._

  def now = System.currentTimeMillis
  
  def countShops(params: QueryParams) = Stats.time("countShops") {
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

  def getShopIds(params: QueryParams, sortKey: String, includeMalls: Boolean) = Stats.time("getShopIds") {
    import params._
    val query = join(
      shops,
      // 没有deal的或者deal过期的商铺也要查出来，所以连接dealShops，deals和dealCategories时使用左外连接。
      dealShops,
      // 当分类条件不存在时，无需连接dealCategories
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty),
      deals,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && brandTag.isEmpty),
      // 查询结果中包含商铺类别，需连接shopCategories，考虑到未分类的商铺，使用左外连接。
      shopCategories.leftOuter,
      // 查询结果中包含品牌rank，需连接brands，某些商铺没有品牌，因此使用左外连接。
      brands.leftOuter,
      brandTags.inhibitWhen(brandTag.isEmpty)
    ) {
      (s, ds, dc, d, db, sc, b, bt) =>
      val order: List[ExpressionNode] = {
        if (sortKey == "score") {
          List(s.score desc, s.rank desc)
        } else {
          List(s.rank desc, s.score desc)
        }
      }
      where(
        // 筛选商铺
        s.cityId === cityId and
        s.districtId === districtId.? and
        s.bizoneId === bizoneId.? and
        s.mallId === mallId.? and
        (s.id <> s.mallId).inhibitWhen(includeMalls) and
        s.brandId === shopBrandId.? and
        // 筛选deal
        (d.timestamp gt now) and
        d.dealType === dealType.? and
        (d.subType in subType).inhibitWhen(subType.isEmpty) and
        (d.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        bitAnd(d.opTag, opTag.?) === opTag.? and
        // 筛选指定的品牌
        db.get.brandId === dealBrandId.? and
        // 根据类别筛选deal
//        dc.get.categoryId === categoryId.? and
//        dc.get.subCateId === subCateId.? and
        ("10000000".~ === categoryId.? or
         dc.get.categoryId <> "10000000") and
        sc.get.categoryId === categoryId.? and
        sc.get.subCateId === subCateId.? and
        // 根据tag筛选品牌
        bt.get.tagId === brandTag.?
      )
      .select(
        s.id,
        s.brandId,
        s.mallId,
        if (b.isEmpty) 0 else b.get.level,
        s.rank match {
          case rank: BigDecimal => rank.toFloat
          case _ => 0f
        },
        sc.map(_.categoryId),
        b.map(_.rank.toFloat).getOrElse(0f),
        s.score match {
          case rank: BigDecimal => rank.toFloat
          case _ => 0f
        },
        s.mall_score match {
          case rank: BigDecimal => rank.toFloat
          case _ => 0f
        }
      )
      .orderBy(order)
      .on(
        // 连接deal-shop关系表
        ds.shopId === s.id,
        // 连接deal-category
        dc.get.dealId === ds.dealId,
        // 连接deal表
        d.id === ds.dealId,
        // 连接deal-brand表
        d.id === db.get.dealId,
        // 连接shop-category
        sc.get.shopId === s.id,
        // 连接品牌表
        b.get.id === s.brandId,
        // 连接品牌Tag表
        db.get.brandId === bt.get.brandId
      )
    }.distinct
    (pager match {
        case Some(pager) => query.page(pager.skip, pager.size).toList
        case None => query.toList
      })
  }

  def getAllShopIds(params: QueryParams, sortKey: String) = Stats.time("getAllShopIds") {
    import params._
    join(
      shops,
      dealShops.leftOuter,
      deals.leftOuter
    ) {
      (s, ds, d) =>
      val orderByScore = (sortKey == "score")
      where(
        s.cityId === cityId and
        s.districtId === districtId.? and
        s.bizoneId === bizoneId.? and
        s.brandId === shopBrandId.?
      )
      .select(s.id)
      .orderBy(
        d.get.id.isNull,
        if (orderByScore) s.score desc else s.rank desc,
        if (orderByScore) s.rank desc else s.score desc
      )
      .on(
        s.id === ds.get.shopId,
        ds.get.dealId === d.get.id and (d.get.timestamp gt now)
      )
    }.distinct.toList
  }

  def getBestShopForDeal(params: QueryParams, dealId: Int) = Stats.time("getBestShopForDeal") {
    import params._
    from(shops, dealShops) {
      (s, ds) =>
      where(
        // 连接条件
        s.id === ds.shopId and
        // 筛选商铺
        s.cityId === cityId and
        s.districtId === districtId.? and
        s.bizoneId === bizoneId.? and
        s.mallId === mallId.? and
        s.brandId === shopBrandId.? and
        ds.dealId === dealId
      )
      .select(ds.dealId, ds.shopId)
      .orderBy(s.score desc, s.rank desc)
    }.headOption.map(_._2)
  }

  def getShopIdsForEval(params: QueryParams, sortKey: String): List[Int] = Stats.time("getShopIdsForEval") {
    import params._
    from(shops, shopCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty))(
      (s, sc) => {
        val order: List[ExpressionNode] = {
          if (sortKey == "score") {
            List(s.score desc, s.rank desc)
          } else {
            List(s.rank desc, s.score desc)
          }
        }
        where(s.id === sc.get.shopId and
              s.cityId === cityId and
              s.bizoneId === bizoneId.? and
              s.districtId === districtId.? and
              sc.get.categoryId === categoryId.? and
              sc.get.subCateId === subCateId.?
        )
        .select(s.id)
        .orderBy(order)
      }
    ).toList
  }
  
  def countShopsGroupByCategory(params: QueryParams)  = {
    countShops[String](params,(sc, d, ds, s, db) => sc.get.categoryId, groupByCate = true)
  }

  def countShopsGroupBySubCate(params: QueryParams): List[(String, Long)] = {
    countShops[String](params, (sc, d, ds, s, db) => sc.get.subCateId, groupByCate = true)
  }

  def countShopsGroupByDistrict(params: QueryParams): List[(Int, Long)] = {
    countShops[Int](
      params, (sc, d, ds, s, db) => s.districtId
    )
  }

  def countShopsGroupByBizone(params: QueryParams): List[(Int, Long)] = {
    countShops[Int](
      params, (sc, d, ds, s, db) => s.bizoneId
    )
  }

  def countShopsGroupByDealType(params: QueryParams): List[(String, Long)] = {
    countShops[Option[String]](
      params, (sc, d, ds, s, db) => d.map(_.dealType)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countShopsGroupBySubType(params: QueryParams): List[(String, Long)] = {
    countShops[Option[String]](
      params, (sc, d, ds, s, db) => d.map(_.subType)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countShopsGroupByMall(params: QueryParams): List[(Int, Long)] = {
    countShops[Option[Int]](
      params, (sc, d, ds, s, db) => s.mallId
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  def countShopsGroupByBrand(params: QueryParams): List[(Int, Long)] = {
    countShops[Option[Int]](
      params, (sc, d, ds, s, db) => s.brandId
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

//  def countShopsGroupByShop(params: QueryParams): List[(Int, Long)]

  def countShopsGroupByBank(params: QueryParams): List[(Int, Long)] = {
    countShops[Option[Int]](
      params, (sc, d, ds, s, db) => d.map(_.bankId)
    ).filter(_._1.nonEmpty).map(i => (i._1.get, i._2))
  }

  private def countShops[K](
    params: QueryParams,
    groupKey: (Option[ShopCategory], Option[Deal], Option[DealShop], Shop, Option[DealBrand]) => TypedExpressionNode[K],
    mallIds: List[Int] = Nil,
    dealBrandIds: List[Int] = Nil,
    shopBrandIds: List[Int] = Nil,
    shopIds: List[Int] = Nil,
    groupByCate: Boolean = false,
    groupByBrand: Boolean = false
  ): List[(K, Long)] = Stats.time("countShopsBy") {
    import params._
    join(
      shops,
      dealShops.leftOuter,
      shopCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty && !groupByCate),
      dealCategories.inhibitWhen(categoryId.isEmpty && subCateId.isEmpty && !groupByCate),
      deals.leftOuter,
      dealBrands.inhibitWhen(dealBrandId.isEmpty && dealBrandIds.isEmpty && brandTag.isEmpty && !groupByBrand),
      brandTags.inhibitWhen(brandTag.isEmpty)
    ) {
      (s, ds, sc, dc, d, db, bt) =>
      val key = groupKey(sc, d, ds, s, db)
      where(
        // 排除过期deal
        (d.get.timestamp gt now) and
        // 指定银行
        (d.get.bankId in bankId).inhibitWhen(bankId.isEmpty) and
        // 指定类型
        d.get.dealType === dealType.? and
        (d.get.dealType <> "30000000" or d.get.subType === "30000003") and
        (d.get.subType in subType).inhibitWhen(subType.isEmpty) and
        // 指定tag
        bitAnd(d.get.opTag, opTag.?) === opTag.? and
        // 指定城市
        (d.get.cityId === 0 or d.get.cityId === cityId or
         s.cityId === cityId) and
        (d.get.subType === "30000003").inhibitWhen(dealType != Some("30000000")) and
        s.districtId === districtId.? and
        s.bizoneId === bizoneId.? and
        sc.get.categoryId === categoryId.? and
        sc.get.subCateId === subCateId.? and
        s.mallId === mallId.? and
        s.brandId === shopBrandId.? and
        (s.brandId in shopBrandIds).inhibitWhen(shopBrandIds.isEmpty) and
        (s.id in shopIds).inhibitWhen(shopIds.isEmpty) and
        (s.mallId in mallIds).inhibitWhen(mallIds.isEmpty) and
        (db.get.brandId === dealBrandId.?) and
        (db.get.brandId in dealBrandIds).inhibitWhen(dealBrandIds.isEmpty) and
        bt.get.tagId === brandTag.?
      )
      .groupBy(key)
      .compute(key, countDistinct(s.id))
      .on(
        s.id === ds.get.shopId and s.cityId === cityId,
        sc.get.shopId === s.id,
        dc.get.dealId === ds.get.dealId,
        ds.get.dealId === d.get.id,
        db.get.dealId === d.get.id,
        bt.get.brandId === db.get.brandId
      )
    }.toList.map(m => (m.measures._1, m.measures._2))
  }

  def countShopsInBrand(params: QueryParams, brandIds: Int*) = Stats.time("countShopsInBrand") {
    import params._
    from(shops)(s =>
      where(s.cityId === cityId and
            s.districtId === districtId.? and
            s.bizoneId === bizoneId.? and
            (s.brandId in brandIds))
      groupBy (s.brandId)
      compute(s.brandId.get, count(s.id))
    ).toList.map(m => (m.measures._1, m.measures._2))
  }
}
