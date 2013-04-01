package com.hui800.listing.db

import com.hui800.listing.db.model._

trait CreditCardQueries {

  def getCardDealList(bankId: Int, cityCode: String, typeCode: String): List[CardDeal]
  
  def countDealsByTypeCode(bankId: Int, cityCode: String): Map[String, Int]
  
  def getCardBrandOrShopList(bankId: Int, cityCode: String): List[CardBrandOrShop]
}
