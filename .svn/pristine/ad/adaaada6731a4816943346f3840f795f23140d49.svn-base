package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._

case class Shop(
  @Column("title") title: String,
  @Column("city_id") cityId: Int,
  @Column("district_id") districtId: Int,
  @Column("bizone_id") bizoneId: Int,
  @Column("mall_id") mallId: Option[Int],
  @Column("brand_id") brandId: Option[Int],
  @Column("rank") rank: BigDecimal,
  @Column("score") score: BigDecimal,
  @Column("mall_score") mall_score:BigDecimal
  ) extends KeyedEntity[Int] {

  val id = 0

  def this() = this(null, 0, 0, 0, Some(0), Some(0), 0, 0,0)
}
