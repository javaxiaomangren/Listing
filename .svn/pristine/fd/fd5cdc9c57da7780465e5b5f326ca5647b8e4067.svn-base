package com.hui800.listing.db.cache

import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.util._

trait ShopQueriesCache extends ShopQueries with Memo with CacheComponent {

  lazy val cachedSpCntGrpBCat    = memoize("spCntGrpBCat",    super.countShopsGroupByCategory _)
  lazy val cachedSpCntGrpBSubCat = memoize("spCntGrpBSubCat", super.countShopsGroupBySubCate _)
  lazy val cachedSpCntGrpBDis    = memoize("spCntGrpBDis",    super.countShopsGroupByDistrict _)
  lazy val cachedSpCntGrpBBiz    = memoize("spCntGrpBBiz",    super.countShopsGroupByBizone _)
  lazy val cachedSpCntGrpBDlTp   = memoize("spCntGrpBDlTp",   super.countShopsGroupByDealType _)
  lazy val cachedSpCntGrpBSubTp  = memoize("spCntGrpBSubTp",  super.countShopsGroupBySubType _)
  lazy val cachedSpCntGrpBMal    = memoize("spCntGrpBMal",    super.countShopsGroupByMall _)
  lazy val cachedSpCntGrpBrd     = memoize("spCntGrpBrd",     super.countShopsGroupByBrand _)
  lazy val cachedSpCntGrpBnk     = memoize("spCntGrpBnk",     super.countShopsGroupByBank _)
  lazy val cachedShopIds         = memoize("spIds",           super.getShopIds _)
  lazy val cachedSpId4Dl         = memoize("spId4Dl",         super.getBestShopForDeal _)
  lazy val cachedSpId4All        = memoize("spId4All",        super.getAllShopIds _)
  lazy val cachedSpIds4Eval      = memoize("spIds4Eval",      super.getShopIdsForEval _)
  
  lazy val cachedSpCntBrd = memoizeList("spCntBrd", super.countShopsInBrand _, (d: (Int, Long)) => d._1)
  
  abstract override def countShopsGroupByCategory(params: QueryParams): List[(String, Long)] = {
    cachedSpCntGrpBCat(params)
  }

  abstract override def countShopsGroupBySubCate(params: QueryParams): List[(String, Long)] = {
    cachedSpCntGrpBSubCat(params)
  }

  abstract override def countShopsGroupByDistrict(params: QueryParams): List[(Int, Long)] = {
    cachedSpCntGrpBDis(params)
  }

  abstract override def countShopsGroupByBizone(params: QueryParams): List[(Int, Long)] = {
    cachedSpCntGrpBBiz(params)
  }

  abstract override def countShopsGroupByDealType(params: QueryParams): List[(String, Long)] = {
    cachedSpCntGrpBDlTp(params)
  }

  abstract override def countShopsGroupBySubType(params: QueryParams): List[(String, Long)] = {
    cachedSpCntGrpBSubTp(params)
  }

  abstract override def countShopsGroupByMall(params: QueryParams): List[(Int, Long)] = {
    cachedSpCntGrpBMal(params)
  }

  abstract override def countShopsGroupByBrand(params: QueryParams): List[(Int, Long)] = {
    cachedSpCntGrpBrd(params)
  }

  abstract override def countShopsGroupByBank(params: QueryParams): List[(Int, Long)] = {
    cachedSpCntGrpBnk(params)
  }

  abstract override def getShopIds(params: QueryParams, sortKey: String, includeMalls: Boolean) = {
    val shopIds = cachedShopIds(params.copy(pager = None), sortKey, includeMalls)
    params.pager match {
      case Some(pager) => shopIds.slice(pager.skip, pager.skip + pager.size)
      case None => shopIds
    }
  }
  
  abstract override def getAllShopIds(params: QueryParams, sortKey: String) = {
    cachedSpId4All(params, sortKey)
  }
  
  abstract override def getBestShopForDeal(params: QueryParams, dealId: Int) = {
    cachedSpId4Dl(params, dealId)
  }
  
  abstract override def getShopIdsForEval(params: QueryParams, sortKey: String) = {
    cachedSpIds4Eval(params, sortKey)
  }

  abstract override def countShopsInBrand(params: QueryParams, brandIds: Int*) = {
    cachedSpCntBrd(params, brandIds: _*)
  }
}
