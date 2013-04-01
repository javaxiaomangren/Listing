package com.hui800.listing.db

import com.hui800.listing.db.model._

trait MallQueries {

  def getShopList(mallId: Int, now: Long): List[MallShopDeal]

  def getBrandList(mallId: Int, floor: Option[Int], now: Long): List[MallShopDeal]

  def getShopListForDeal(dealId: Int): List[MallShopDeal]
}
