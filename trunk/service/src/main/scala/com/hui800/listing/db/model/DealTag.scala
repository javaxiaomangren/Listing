package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._

case class DealTag(
  @Column("deal_id") dealId: Int,
  @Column("tag")     tag:    String
)
