package com.hui800.listing.db.model

import org.squeryl.annotations._

case class CardDeal(
  @Column("id")          id:         Int    = 0,
  @Column("bank_id")     bankId:     Int    = 0,
  @Column("type_code")   typeCode:   String = null,
  @Column("create_time") createTime: Long   = 0,
  @Column("city_code")   cityCode:   String = null,
  @Column("deadline")    deadline:   Option[Long] = None
) {
  def this() = this(0, 0, null, 0, null, Some(0L))
}
