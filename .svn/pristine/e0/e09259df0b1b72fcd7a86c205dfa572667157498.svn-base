package com.hui800.listing.svc.cache

import com.hui800.listing.svc._
import com.hui800.listing.svc.model._
import com.hui800.listing.cache._
import com.hui800.listing.db._

trait ListServiceCache extends ListService with Memo with CacheComponent {

  lazy val cachedList = memoize("list", super.list _)
  lazy val cachedShopIdList = memoize("dp", super.shopIdList _)
  lazy val cachedAggregate = memoize("aggr", super.aggregate _)
  
  abstract override def list(params: Parameters): FinalResult = {
    cachedList(params)
  }
  
  abstract override def shopIdList(params: Parameters): Result = {
    cachedShopIdList(params)
  }
  
  abstract override def aggregate(query: QueryParams, sortKey: String, shopOnly: Boolean) = {
    cachedAggregate(query, sortKey, shopOnly)
  }
}
