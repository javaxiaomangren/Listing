package com.hui800.listing.db.cache

import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.util._

trait DealQueriesCache extends DealQueries with Memo with CacheComponent {

  lazy val cachedDealIds        = memoize("dlIds",          super.getDealIds _)
  lazy val cachedDlAndBnkIds    = memoize("dlAndBnkIds",    super.getDealAndBankIds _)
  lazy val cachedDlCntGrpCat    = memoize("dlCntGrpCat",    super.countDealsGroupByCategory _)
  lazy val cachedDlCntGrpSub    = memoize("dlCntGrpSub",    super.countDealsGroupBySubCate _)
  lazy val cachedDlCntGrpTyp    = memoize("dlCntGrpTyp",    super.countDealsGroupByDealType _)
  lazy val cachedDlCntGrpSubTyp = memoize("dlCntGrpSubTyp", super.countDealsGroupBySubType _)
  lazy val cachedDlCntGrpDis    = memoize("dlCntGrpDis",    super.countDealsGroupByDistrict _)
  lazy val cachedDlCntGrpBiz    = memoize("dlCntGrpBiz",    super.countDealsGroupByBizone _)
  lazy val cachedDlCntGrpMal    = memoize("dlCntGrpMal",    super.countDealsGroupByMall _)
  lazy val cachedDlCntGrpBrd    = memoize("dlCntGrpBrd",    super.countDealsGroupByBrand _)
  lazy val cachedDlCntGrpBnk    = memoize("dlCntGrpBnk",    super.countDealsGroupByBank _)
  lazy val cachedDlCntGrpShp    = memoize("dlCntGrpShp",    super.countDealsGroupByShop _)
  lazy val cachedDlCnt          = memoize("dlCnt",          super.countDeals _)
  
  lazy val cachedDlCntMal   = memoizeList("dlCntMal",   super.countDealsInMall _,      (d: (Int, Long)) => d._1)
  lazy val cachedDlCntDlBrd = memoizeList("dlCntDlBrd", super.countDealsInDealBrand _, (d: (Int, Long)) => d._1)
  lazy val cachedDlCntSpBrd = memoizeList("dlCntSpBrd", super.countDealsInShopBrand _, (d: (Int, Long)) => d._1)
  lazy val cachedDlCntShp   = memoizeList("dlCntShp",   super.countDealsInShop _,      (d: (Int, Long)) => d._1)
  lazy val cachedStypBnkIds = memoizeList("stypBnkIds", super.getSubTypeAndBankIds _,  (d: (Int, String, Int)) => d._1)
  lazy val cachedDeals      = memoizeList("dls",        super.getDeals _,              (d: Deal) => d.id)

  abstract override def getDealIds(params: QueryParams, sortKey:String): List[Int] = {
    val dealIds = cachedDealIds(params.copy(pager = None), sortKey)
    params.pager match {
      case Some(pager) => dealIds.slice(pager.skip, pager.skip + pager.size)
      case None => dealIds
    }
  }
  
  abstract override def getDealAndBankIds(params: QueryParams, sortKey: String) = {
    cachedDlAndBnkIds(params, sortKey)
  }
  
  abstract override def countDealsGroupByCategory(params: QueryParams) = {
    cachedDlCntGrpCat(params)
  }
  
  abstract override def countDealsGroupBySubCate(params: QueryParams) = {
    cachedDlCntGrpSub(params)
  }
  
  abstract override def countDealsGroupByDealType(params: QueryParams) = {
    cachedDlCntGrpTyp(params)
  }
  
  abstract override def countDealsGroupBySubType(params: QueryParams) = {
    cachedDlCntGrpSubTyp(params)
  }
  
  abstract override def countDealsGroupByDistrict(params: QueryParams) = {
    cachedDlCntGrpDis(params)
  }
  
  abstract override def countDealsGroupByBizone(params: QueryParams) = {
    cachedDlCntGrpBiz(params)
  }
  
  abstract override def countDealsGroupByMall(params: QueryParams) = {
    cachedDlCntGrpMal(params)
  }
  
  abstract override def countDealsGroupByBrand(params: QueryParams) =  {
    cachedDlCntGrpBrd(params)
  }
  
  abstract override def countDealsGroupByBank(params: QueryParams) =  {
    cachedDlCntGrpBnk(params)
  }
  
  abstract override def countDealsGroupByShop(params: QueryParams) = {
    cachedDlCntGrpShp(params)
  }

  abstract override def countDealsInMall(params: QueryParams, mallIds: Int*) = {
    cachedDlCntMal(params, mallIds: _*)
  }
  
  abstract override def countDealsInDealBrand(params: QueryParams, dealBrandIds: Int*) =  {
    cachedDlCntDlBrd(params, dealBrandIds: _*)
  }
  
  abstract override def countDealsInShopBrand(params: QueryParams, shopBrandIds: Int*) =  {
    cachedDlCntSpBrd(params, shopBrandIds: _*)
  }
  
  abstract override def countDealsInShop(params: QueryParams, shopIds: Int*) = {
    cachedDlCntShp(params, shopIds: _*)
  }
  
  abstract override def countDeals(params: QueryParams) = {
    cachedDlCnt(params)
  }
  
  abstract override def getDeals(dealIds: Int*) = {
    cachedDeals(dealIds:_*)
  }

  abstract override def getSubTypeAndBankIds(dealIds: Int*) = {
    cachedStypBnkIds(dealIds: _*)
  }
}
