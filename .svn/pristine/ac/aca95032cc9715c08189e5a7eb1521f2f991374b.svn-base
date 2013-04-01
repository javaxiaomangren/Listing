package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._

case class Brand(
                  @Column("title") title: String,
                  @Column("level") level: Int,
                  @Column("rank") rank: BigDecimal
                  ) extends KeyedEntity[Int] {
  val id = 0
  def this() = this(null, 0, 0)
}
