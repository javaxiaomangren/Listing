package com.hui800.listing.db.impl

import com.hui800.listing.db._
import com.twitter.ostrich.stats.Stats
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2

class RegionQueriesImpl(db: DB) extends RegionQueries {

  import db._
  
  def getRegion(depth: Int, subId: Int) = Stats.time("getRegion") {
    regions.lookup(CompositeKey2(depth, subId))
  }
}
