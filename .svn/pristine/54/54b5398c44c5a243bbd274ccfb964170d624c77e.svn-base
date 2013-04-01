package com.hui800.listing.svc.model

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonAutoDetect._
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility._
import com.fasterxml.jackson.annotation.JsonInclude._
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion._

@JsonAutoDetect(fieldVisibility = ANY)
@JsonInclude(Include.NON_NULL)
@deprecated
case class Item @JsonCreator()
(
  @JsonProperty("mall_id")
  mall_id: Int = 0,
  @JsonProperty("brand_id")
  val brand_id: Int = 0,
  @JsonProperty("shop_id")
  val shop_id: Int = 0,
  @JsonProperty("deal_id")
  val deal_id: Int = 0,
  @JsonProperty("bank_id")
  val bank_id: Int = 0,
  @JsonProperty("shop_count")
  val shop_count: Long = 0,
  @JsonProperty("deal_count")
  val deal_count: Any = 0,
  val rank: Float = 0,
  val score: Float = 0,
  val deals: List[Int] = null,
  val shop_ids: List[Int] = null
  ) {
  def this() = this(0, 0, 0, 0, 0, 0, 0, 0,0,null, null)
  def toMap = Map(
    "mall_id" -> zeroToNull(mall_id, 0),
    "brand_id" -> zeroToNull(brand_id, 0),
    "shop_id" -> zeroToNull(shop_id, 0),
    "deal_id" -> zeroToNull(deal_id, 0),
    "bank_id" -> zeroToNull(bank_id, 0),
    "shop_count" -> zeroToNull(shop_count, 0),
    "deal_count" -> zeroToNull(deal_count, 0),
    "rank" -> zeroToNull(rank, 0),
    "score" -> zeroToNull(score, 0),
    "deals" -> deals,
    "shop_ids" -> shop_ids
  ).filterNot(_._2 == null)
  private def zeroToNull[T](value: T, zero: T) = {
    if (value == zero) null else value
  }
}
