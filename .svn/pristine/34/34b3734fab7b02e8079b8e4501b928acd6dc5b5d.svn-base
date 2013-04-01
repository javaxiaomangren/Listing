package com.hui800.listing.db.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

class CreditCardQueriesImpl(db: DB) extends CreditCardQueries {

  def getCardDealList(
    bankId:   Int,
    cityCode: String,
    typeCode: String
  ) = {
    from(db.cardDeals)(d =>
      where(
        d.bankId   === bankId   and
        d.cityCode === cityCode and
        (d.typeCode like typeCode)
      )
      select(
        CardDeal(
          id         = d.id,
          createTime = d.createTime,
          deadline   = d.deadline
        )
      )
      orderBy(d.createTime desc)
    ).toList
  }
  
  def countDealsByTypeCode(
    bankId:   Int,
    cityCode: String
  ) = {
    from(db.cardDeals)(d =>
      where(
        d.bankId   === bankId and
        d.cityCode === cityCode
      )
      groupBy(
        d.typeCode
      )
      compute(
        count(d.id)
      )
    ).map(
      m => (m.key.toString -> m.measures.toInt)
    ).toMap
  }
  
  def getCardBrandOrShopList(
    bankId:   Int,
    cityCode: String
  ) = {
    from(db.cardBrandOrShops)(bs => 
      where(
        bs.bankId   === bankId   and
        bs.cityCode === cityCode
      )
      select(
        CardBrandOrShop(
          typeCode = bs.typeCode,
          id       = bs.id,
          deadline = bs.deadline,
          category = bs.category,
          dealId   = bs.dealId
        )
      )
      orderBy(
        bs.typeCode   asc,
        bs.createTime desc
      )
    ).toList
  }
}
