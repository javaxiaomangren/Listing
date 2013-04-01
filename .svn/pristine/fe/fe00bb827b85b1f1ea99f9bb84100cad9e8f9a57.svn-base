package com.hui800.listing.db.cache

import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.model._

trait CreditCardQueriesCache extends CreditCardQueries with Memo with CacheComponent {

  lazy val cardDealListCache = memoize("card.dlLst", super.getCardDealList _)
  lazy val dealCountByTypeCache = memoize("card.dlCntByTyp", super.countDealsByTypeCode _)
  lazy val bosListCache = memoize("card.bosList", super.getCardBrandOrShopList _)
  
  abstract override def getCardDealList(bankId: Int, cityCode: String, typeCode: String) = {
    cardDealListCache(bankId, cityCode, typeCode)
  }
  
  abstract override def countDealsByTypeCode(bankId: Int, cityCode: String) = {
    dealCountByTypeCache(bankId, cityCode)
  }
  
  abstract override def getCardBrandOrShopList(bankId: Int, cityCode: String) = {
    bosListCache(bankId, cityCode)
  }
}
