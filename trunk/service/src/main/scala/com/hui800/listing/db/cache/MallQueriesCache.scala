package com.hui800.listing.db.cache

import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.model._

trait MallQueriesCache extends MallQueries with Memo with CacheComponent {

//  lazy val brandListCache = memoize("mall.brdLst", super.getBrandList _)
//  lazy val shopListCache = memoize("mall.spLst", super.getShopList _)
  lazy val shopListPerDealCache = memoize("mall.spLitPerDl", super.getShopListForDeal _)
  
//  abstract override def getBrandList(mallId: Int, floor: Option[Int], now: Long) = {
//    brandListCache(mallId, floor, now)
//  }
//
//  abstract override def getShopList(mallId: Int, now: Long) = {
//    shopListCache(mallId, now)
//  }
  
  abstract override def getShopListForDeal(dealId: Int) = {
    shopListPerDealCache(dealId)
  }
}
