package com.hui800.listing.db

import com.hui800.listing.db.model._

trait RegionQueries {

  def getRegion(depth: Int, subId: Int): Option[Region]
}
