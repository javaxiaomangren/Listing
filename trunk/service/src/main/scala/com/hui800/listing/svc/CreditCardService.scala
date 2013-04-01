package com.hui800.listing.svc

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.util._

trait CreditCardService {

  def listCardDeals(
    cityCode: String,
    bankId:   Int,
    typeCode: String,
    now:      Long,
    excludes: Set[Int],
    pager:    Pager
  ): (Int, List[CardDeal])
  
  def countCardDeals(
    cityCode: String,
    bankId:   Int
  ): Map[String, Int]

  def listCardBrandOrShop(
    cityCode: String,
    bankId:   Int,
    category: Option[String],
    subcate:  Option[String],
    now:      Long,
    pager:    Pager
  ): (Int, List[CardBrandOrShop])
  
  def countBrandOrShopsByCategory(
    cityCode: String,
    bankId:   Int,
    level:    Int
  ): Map[String, Int]
}
