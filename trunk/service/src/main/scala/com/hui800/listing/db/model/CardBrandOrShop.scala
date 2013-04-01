package com.hui800.listing.db.model
  
import org.squeryl.annotations._

case class CardBrandOrShop(
  @Column("city_code")   cityCode:   String = null,
  @Column("bank_id")     bankId:     Int    = 0,
  @Column("type_code")   typeCode:   String = null,
  @Column("id")          id:         Int    = 0,
  @Column("category")    category:   String = null,
  @Column("create_time") createTime: Long   = 0,
  @Column("deadline")    deadline:   Option[Long] = None,
  @Column("deal_id")     dealId:     String = null
) {
  
  @Transient @transient lazy val dealList: List[(Int, Int)] = dealId match {
    case dealIds: String => dealIds.split(",").map(_.toInt).grouped(2).map(e => (e(0), e(1))).toList
    case _ => Nil
  }
  @Transient @transient lazy val categorySet: Set[String] = category match {
    case category: String => category.split(",").toSet map {
        c: String => if (c.length < 2) (c + '0') else c
      }
    case _ => Set.empty
  }
  def this() = this(null, 0, null, 0, null, 0, Some(0L), null)
}
