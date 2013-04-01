package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import org.squeryl.dsl._

case class ChannelDeal(
  @Column("channel_id") channelId: Int,
  @Column("deal_id")    dealId:    Int
) extends KeyedEntity[CompositeKey2[Int,Int]] {
  def id = CompositeKey2(channelId, dealId)
  def this() = this(0, 0)
}
