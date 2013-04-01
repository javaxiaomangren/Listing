package com.hui800.listing.db.model

import org.squeryl.annotations._

case class MallShopDeal(
  @Column("shop_id")      shopId:     Int = 0,
  @Column("deal_id")      dealId:     Int = 0,
  @Column("mall_deal_id") mallDealId:   Int = 0,
  @Column("from_time")    fromTime:   Long = 0,
  @Column("to_time")      toTime:     Option[Long] = Some(0L),
  @Column("priority")     priority:   Int = 0,
  @Column("create_time")  createTime: Long = 0,
  @Column("mall_id")      mallId:     Int = 0,
  @Column("floor_id")     floorId:    Int = 0,
  @Column("floor_order")  floorOrder: Int = 0,
  @Column("subcates")     subcates:   String = null,
  @Column("brand_id")     brandId:    Int = 0,
  @Column("brand_rank")   brandRank:  Int = 0,
  @Column("py_name")      pinyin:     String = null,
  @Transient              deals:      List[MallDeal] = null
) {
  
  @Transient @transient lazy val subcateList = Option(subcates).map(
    _.split(",").filter(_.nonEmpty).toList
  ).getOrElse(Nil)
  
  def this() = this(toTime = Some(0L))
}

case class MallDeal(
  id:       Int,
  priority: Int,
  fromTime: Long,
  toTime:   Long
)
