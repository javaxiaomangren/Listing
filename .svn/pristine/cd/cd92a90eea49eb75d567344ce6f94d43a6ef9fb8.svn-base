package com.hui800.listing.svc.impl

import com.hui800.listing.db._
import com.hui800.listing.svc._
import com.hui800.listing.svc.model._
import com.twitter.logging.Logger
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import scala.collection.mutable.LinkedHashMap
import scala.collection.JavaConversions._
import java.util.concurrent.atomic.AtomicInteger

class ListServiceImpl(
  dealQueries: DealQueries,
  shopQueries: ShopQueries,
  regionService: RegionService,
  shopCountService: CountService,
  dealCountService: CountService
) extends ListService {

  val logger = Logger(getClass.getName)
  
  def list(params: Parameters): FinalResult = inTransaction {
    import params._
    val (city, district, bizone) = regionService.resolve(cityId, districtId, bizoneId)
    val origin = QueryParams(
      cityId = city,
      districtId = district,
      bizoneId = bizone,
      categoryId = categoryId,
      subCateId = subCateId,
      mallId = mallId,
      bankId = bankId,
      dealType = dealType,
      subType = subType,
      dealBrandId = dealBrand,
      shopBrandId = shopBrand,
      opTag = opTag,
      brandTag = brandTag
    )
    listStart(origin, content, pager, sortKey.getOrElse("score"), debug)
  }
  
  def shopIdList(params: Parameters): Result = inTransaction {
    import params._
    val (city, district, bizone) = regionService.resolve(cityId, districtId, bizoneId)
    val origin = QueryParams(
      cityId = city,
      districtId = district,
      bizoneId = bizone,
      categoryId = categoryId,
      subCateId = subCateId
    )
    getShopIdForDPStart(origin, pager, sortKey.getOrElse("score"), debug)
  }
  
  def getShopIdForDPStart(
    origin: QueryParams, pager: Pager, sortKey: String, debug: Boolean
  ) = inTransaction {
    if (debug) {
      Session.currentSession.setLogger(logger.debug(_))
    }
    val items = shopQueries.getShopIdsForEval(origin, sortKey);
    Result(0, items.size, items)
  }
  
  private def listStart(
    origin: QueryParams, content: String, pager: Pager, sortKey: String, debug: Boolean
  ) = inTransaction {
    if (debug) {
      Session.currentSession.setLogger(logger.debug(_))
    }
    val dealType = if (List("30000001", "30000004", "30000005").contains(origin.subType.headOption.getOrElse(""))) {
      "dlNoSp"
    } else if (origin.subType == Some("30000003")) {
      "dlWithSp"
    } else if (origin.dealType == Some("30000000")) {
      "both"
    } else {
      "dlWithSp"
    }
    (content, dealType) match {
      case ("brand", "dlWithSp") =>
        brandList(origin, pager, sortKey)
      case ("brand", "both") =>
        brandOrBankList(origin, pager, sortKey)
      case ("brand", "dlNoSp") =>
        bankList(origin, pager, sortKey)
      case ("shop", _) =>
        shopList(origin, pager, sortKey)
      case ("dealShop", _) =>
        dealShopList(origin, pager)
      case ("allShops", _) =>
        allShopList(origin, pager, sortKey)
      case ("brandOrBank", _) =>
        brandOrBankList(origin, pager, sortKey)
    }
  }
  
  private def allShopList(origin: QueryParams, pager: Pager, sortKey: String) = {
    val allShopIds = shopQueries.getAllShopIds(origin, sortKey)
    val items = allShopIds.map(shopId => {
        Item(shop_id = shopId)
      }).slice(pager.skip, pager.skip + pager.size)
    val itemsWithCount = withCount(items, origin)
    val itemsWithDeals = withDeal(itemsWithCount, origin)
    FinalResult(
      Result(Pager.numOfPages(allShopIds.size, pager.size), allShopIds.size, itemsWithDeals),
      categoryCounts(origin, shopCountService).toArray,
      districtCounts(origin, shopCountService).toArray,
      dealTypeCounts(origin, shopCountService).toArray,
      bankCounts(origin, shopCountService).toArray,
      brandCounts(origin, shopCountService).toArray,
      mallCounts(origin, shopCountService).toArray
    )
  }
  
  def dealShopList(origin: QueryParams, pager: Pager) = {
    val dealIds = dealQueries.getDealIds(origin, "time")
    val totalCount = dealIds.size
    val items = dealIds.slice(pager.skip, pager.skip + pager.size)
    val itemsWithShops = items map {
      did =>
      shopQueries.getBestShopForDeal(QueryParams(
          cityId = origin.cityId,
          districtId = origin.districtId,
          bizoneId = origin.bizoneId,
          mallId = origin.mallId,
          dealBrandId = origin.dealBrandId
        ), did) match {
        case Some(shopId) =>
          Item(deal_id = did, shop_id = shopId)
        case None =>
          Item(deal_id = did)
      }
    }
    FinalResult(
      Result(Pager.numOfPages(totalCount, pager.size), totalCount, itemsWithShops),
      categoryCounts(origin, dealCountService).toArray,
      districtCounts(origin, dealCountService).toArray,
      dealTypeCounts(origin, dealCountService).toArray,
      bankCounts(origin, dealCountService).toArray,
      brandCounts(origin, dealCountService).toArray,
      mallCounts(origin, dealCountService).toArray
    )
  }

  private def brandList(origin: QueryParams, pager: Pager, sortKey: String) = {
    val shops = aggregate(origin, sortKey)
    val totalCount = shops.size
    val items = shops.slice(pager.skip, pager.skip + pager.size)
    val itemsWithCount = withCount(items, origin)
    val itemsWithShop = withShop(itemsWithCount, origin, sortKey)
    val itemsWithDeals = withDeal(itemsWithShop, origin)
    FinalResult(
      Result(Pager.numOfPages(totalCount, pager.size), totalCount, itemsWithDeals),
      categoryCounts(origin, shopCountService).toArray,
      districtCounts(origin, shopCountService).toArray,
      dealTypeCounts(origin, shopCountService).toArray,
      shopCountsByBank(origin, shopCountService).toArray,
      brandCounts(origin, shopCountService).toArray,
      mallCounts(origin, shopCountService).toArray
    )
  }
  
  private def brandOrBankList(origin: QueryParams, pager: Pager, sortKey: String) = {
    val bankResult = bankList(origin, pager, sortKey)
    val brandResult = if (bankResult.list.count < pager.size + pager.skip) {
      brandList(origin, pager.copy(skip = pager.skip - bankResult.list.count), sortKey)
    } else if (bankResult.list.items.size < pager.size) {
      brandList(origin, Pager(0, pager.size - bankResult.list.items.size), sortKey)
    } else {
      brandList(origin, Pager(0, 1), sortKey)
    }
    val totalCount = bankResult.list.count + brandResult.list.count
    val items = (bankResult.list.items.toArray ++ brandResult.list.items).toList
    FinalResult(
      Result(Pager.numOfPages(totalCount, pager.size), totalCount, items),
      categoryCounts(origin, shopCountService).toArray,
      districtCounts(origin, shopCountService).toArray,
      dealTypeCounts(origin, shopCountService).toArray,
      shopCountsByBank(origin, shopCountService).toArray,
      brandCounts(origin, shopCountService).toArray,
      mallCounts(origin, shopCountService).toArray
    )
  }

  private def bankList(origin: QueryParams, pager: Pager, sortKey: String) = {
    val dealAndBankIds = dealQueries.getDealAndBankIds(origin, sortKey)
    val items = LinkedHashMap[Int, Item]()
    val db = dealAndBankIds.iterator
    while (db.hasNext) {
      val (dealId, bankId) = db.next
      items.getOrElseUpdate(
        bankId, Item(bank_id = bankId, deal_id = dealId, deal_count = new AtomicInteger(0))
      ).deal_count.asInstanceOf[AtomicInteger].incrementAndGet
    }
    val totalCount = items.size
    FinalResult(
      Result(Pager.numOfPages(totalCount, pager.size), totalCount, items.slice(pager.skip, pager.skip + pager.size).values.toList),
      categoryCounts(origin, shopCountService).toArray,
      districtCounts(origin, shopCountService).toArray,
      dealTypeCounts(origin, shopCountService).toArray,
      shopCountsByBank(origin, shopCountService).toArray,
      brandCounts(origin, shopCountService).toArray,
      mallCounts(origin, shopCountService).toArray
    )
  }
  
  private def shopList(origin: QueryParams, pager: Pager, sortKey: String) = {
    val shops = aggregate(origin, sortKey)
    val totalCount = shops.size
    val items = shops.slice(pager.skip, pager.skip + pager.size)
    val itemsWithCount = withCount(items, origin)
    val itemsWithDeals = withDeal(itemsWithCount, origin)
    FinalResult(
      Result(Pager.numOfPages(totalCount, pager.size), totalCount, itemsWithDeals),
      categoryCounts(origin, shopCountService).toArray,
      districtCounts(origin, shopCountService).toArray,
      dealTypeCounts(origin, shopCountService).toArray,
      shopCountsByBank(origin, shopCountService).toArray,
      brandCounts(origin, shopCountService).toArray,
      mallCounts(origin, shopCountService).toArray
    )
  }

  def aggregate(query: QueryParams, sortKey: String, shopOnly: Boolean = false) = {
    val shop = shopQueries.getShopIds(query, sortKey)
    val s = shop.iterator
    val shopCount = scala.collection.mutable.Map[Int, Int]()
    val showHas = shop map { s =>
      if(!shopOnly){
        s match{
          case (shopId, Some(brandId), _, _, shop_rank, _, brand_rank, score,_) 
            if (sortKey != "rank") =>
            val count = shopCount.getOrElseUpdate(brandId, 0)
            shopCount.put(brandId, count + 1)
          case _ => 
        }
      }
    }
    
    val items = LinkedHashMap[(Int, Int, Int), Item]()
    while (s.hasNext) {
      if (shopOnly) {
        s.next match {
          case (shopId, _, _, _, _, _, _, _,mall_score) =>
            items.getOrElseUpdate(
              (shopId, 0, 0),
              Item(shop_id = shopId)
            )
        }
      } else {
        s.next match {
          case (_, _, Some(mallId), _, _, _, _, _, mall_score) =>
            // 商场店铺聚合到商场
            items.getOrElseUpdate(
              (0, 0, mallId),
              Item(mall_id = mallId, score = mall_score)
            )
          case (shopId, Some(brandId), _, _, shop_rank, _, brand_rank, score,_)
            if (sortKey != "rank" && shopCount.get(brandId).get > 5) => //这行有bug ?  scala.None$.get(Option.scala:272)
            // 品牌聚合到品牌
            val i = items.getOrElseUpdate(
              (0, brandId, 0),
              Item(brand_id = brandId, shop_id = shopId, rank = shop_rank, score = score)
            )
            if (i.shop_id != shopId) {
              items.put((0, brandId, 0), i.copy(rank = brand_rank))
            }
          case (shopId, _, _, _, shop_rank, _, brand_rank, score,_) =>
            // 没有品牌的商铺不进行聚合
            items.getOrElseUpdate(
              (shopId, 0, 0),
              Item(shop_id = shopId, rank = shop_rank, score = score)
            )
        }
      }
    }
    items.values.toList
  }
  
  private def withCount(items: List[Item], origin: QueryParams) = {
    val brandIds = items collect {
      case Item(_, brandId, _, _, _, _, _, _, _, _,_)
        if (brandId != 0) =>
        brandId
    }
    val mallIds = items collect {
      case Item(mallId, _, _, _, _, _, _, _, _, _,_)
        if (mallId != 0) =>
        mallId
    }
    val shopIds = items collect {
      case Item(_, 0, shopId, _, _, _, _, _, _, _,_)
        if (shopId != 0) =>
        shopId
    }
    val spCnt = shopQueries.countShopsInBrand(origin, brandIds: _*).toMap
    val dlCntMal = dealQueries.countDealsInMall(origin, mallIds: _*).toMap
    val dlCntBrd = dealQueries.countDealsInShopBrand(origin, brandIds: _*).toMap
    val dlCntShp = dealQueries.countDealsInShop(origin, shopIds ++ mallIds: _*).toMap
    items map {
      case item@Item(mallId, _, _, _, _, _, _, _, _, _,_)
        if (mallId != 0) =>
        item.copy(
          deal_count = dlCntMal.get(mallId).getOrElse(1L)
        )
      case item@Item(_, brandId, _, _, _, _, _, _, _, _,_)
        if (brandId != 0) =>
        item.copy(
          deal_count = dlCntBrd.get(brandId).getOrElse(0),
          shop_count = spCnt.get(brandId).getOrElse(1)
        )
      case item@Item(_, _, shopId, _, _, _, _, _, _, _,_)
        if (shopId != 0) =>
        item.copy(
          deal_count = dlCntShp.get(shopId).getOrElse(0)
        )
      case item =>
        item
    }
  }
  
  private def withShop(items: List[Item], origin: QueryParams, sortKey: String) = {
    items map {
      case item@Item(_, brandId, shopId, _, _, shopCount, _, _, _, _,_)
        if (brandId != 0 &&
            (shopId != 0 && shopCount > 1 ||
             shopId == 0 && shopCount > 0)) =>
        val shop_ids = shopQueries.getAllShopIds(
          QueryParams(
            cityId = origin.cityId,
            districtId = origin.districtId,
            bizoneId = origin.bizoneId,
            shopBrandId = Some(brandId)
          ),
          sortKey
        ).slice(0, 4).filter(_ != shopId).slice(0,3)
        item.copy(shop_ids = shop_ids)
      case item => item
    }
  }

  private def withDeal(items: List[Item], origin: QueryParams) = {
    items map {
      case item@Item(_, _, _, _, _, _, 0, _, _, _,_) =>
        item
      case item@Item(mallId, _, _, _, _, _, _, _, _, _,_)
        if (mallId != 0) =>
        val mallDeals = dealQueries.getDealIds(
          origin.copy(mallId = Some(mallId))
        ).slice(0, 3)
        item.copy(deals = mallDeals)
      case item@Item(_, _, shopId, _, _, _, _, _, _, _,_)
        if (shopId != 0) =>
        item.copy(deals = dealQueries.getDealIds(origin.copy(shopId = Some(shopId))).slice(0, 3))
      case item@Item(_, brandId, _, _, _, _, _, _, _, _,_)
        if (brandId != 0) =>
        item.copy(deals = dealQueries.getDealIds(origin.copy(shopBrandId = Some(brandId))).slice(0, 3))
      case item =>
        item
    }
  }
  
  private def categoryCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.categoryId match {
      case Some("10000000") =>
        countService.countByCategory(
          origin.copy(categoryId = None, subCateId = None),
          Some("70000000", countService.countBySubCate(origin.copy(categoryId = Some("70000000"), subCateId = None)))
        )
      case Some(categoryId) =>
        countService.countByCategory(
          origin.copy(categoryId = None, subCateId = None),
          Some(categoryId, countService.countBySubCate(origin.copy(subCateId = None)))
        )
      case None =>
        countService.countByCategory(origin.copy(subCateId = None))
    }
  }
  
  
  private def mallCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    if (origin.categoryId == Some("10000000") ||
        origin.subCateId == Some("70000001")) {
      countService.countBySubCate(origin.copy(categoryId = Some("10000000"), subCateId = None))
    } else {
      Nil
    }
  }

  private def districtCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.districtId match {
      case Some(districtId) =>
        countService.countByDistrict(
          origin.copy(districtId = None, bizoneId = None),
          Some(districtId, countService.countByBizone(origin.copy(bizoneId = None)))
        )
      case None =>
        countService.countByDistrict(origin.copy(bizoneId = None))
    }
  }

  private def dealTypeCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.dealType match {
      case Some(dealType) =>
        countService.countByDealType(
          origin.copy(dealType = None, subType = Nil),
          Some(dealType, countService.countBySubType(origin.copy(subType = Nil)))
        )
      case None =>
        countService.countByDealType(origin.copy(subType = Nil))
    }
  }

  private def brandCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.categoryId match {
      case Some("10000000") =>
        countService.countByBrand(origin.copy(dealBrandId = None))
      case _ =>
        Nil
    }
  }
  
  private def bankCounts(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.dealType match {
      case Some("30000000") =>
        countService.countByBank(origin.copy(bankId = Nil))
      case _ =>
        Nil
    }
  }

  private def shopCountsByBank(origin: QueryParams, countService: CountService): List[MidItem] = {
    origin.dealType match {
      case Some("30000000") =>
        countService.countByBank(origin.copy(bankId = Nil))
      case _ =>
        Nil
    }
  }
}
