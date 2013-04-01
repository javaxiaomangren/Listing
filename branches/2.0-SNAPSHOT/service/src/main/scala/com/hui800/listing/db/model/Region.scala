package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import org.squeryl.dsl._

case class Region(
  @Column("depth")       depth:      Int,
  @Column("sub_id")      subId:      Int,
  @Column("name")        name:       String,
  @Column("city_id")     cityId:     Int,
  @Column("district_id") districtId: Int
) extends KeyedEntity[CompositeKey2[Int,Int]] {
  def id = CompositeKey2(depth, subId)
  def this() = this(0, 0, null, 0, 0)
}
