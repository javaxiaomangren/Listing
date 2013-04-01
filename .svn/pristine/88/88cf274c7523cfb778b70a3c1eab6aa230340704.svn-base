package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import org.squeryl.dsl._

case class DealShop(
  @Column("deal_id") dealId: Int,
  @Column("shop_id") shopId: Int
) extends KeyedEntity[CompositeKey2[Int,Int]] {
  def id = CompositeKey2(dealId, shopId)
  def this() = this(0, 0)
}
