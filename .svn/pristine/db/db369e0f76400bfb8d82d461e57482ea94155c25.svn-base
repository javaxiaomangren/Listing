package com.hui800.listing.db.cache

import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.model._

trait RegionQueriesCache extends RegionQueries with Memo with CacheComponent {

  lazy val cachedRegions  = memoize("rgns", super.getRegion _)

  abstract override def getRegion(depth: Int, subId: Int) = cachedRegions(depth, subId)
}
