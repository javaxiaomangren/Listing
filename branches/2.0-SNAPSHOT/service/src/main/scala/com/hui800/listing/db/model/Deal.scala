package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._

case class Deal(
  @Column("deal_type")  dealType:   String,
  @Column("sub_type")   subType:    String,
  @Column("site_name")  source:     String,
  @Column("timestamp")  timestamp:  Long,
  @Column("popularity") popularity: Int,
  @Column("type_order") typeOrder:  Int,
  @Column("bank_id")    bankId:     Int,
  @Column("operate_tag")opTag:      Int,
  @Column("city")       cityId:     Int,
  @Column("mall_deal")  isMallDeal: Boolean
) extends KeyedEntity[Int] {
  val id = 0
  def this() = this(null, null, null, 0, 0, 0, 0, 0, 0, false)
}
