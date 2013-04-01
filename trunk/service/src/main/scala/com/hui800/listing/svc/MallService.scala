package com.hui800.listing.svc

import com.hui800.listing.db.model._

trait MallService {

  def shopsInMall(
    mallId:  Int,
    floor:   Option[Int],
    subcate: Option[String],
    letter:  Option[String],
    now:     Long
  ): List[MallShopDeal]

  def brandsInMall(
    mallId:  Int,
    floor:   Option[Int],
    subcate: Option[String],
    letter:  Option[String],
    now:     Long
  ): List[MallShopDeal]

  def shopsForDeal(
    dealId: Int,
    now:    Long
  ): List[MallShopDeal]

  def countByFloor(shops: List[MallShopDeal]): Map[_, Int]

  def countBySubcate(shops: List[MallShopDeal]): Map[String, Int]

  def countByLetter(shops: List[MallShopDeal]): Map[String, Int]
}
