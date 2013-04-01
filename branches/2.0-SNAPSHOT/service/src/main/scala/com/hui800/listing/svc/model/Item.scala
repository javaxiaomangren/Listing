package com.hui800.listing.svc.model

import org.codehaus.jackson.annotate._
import org.codehaus.jackson.annotate.JsonAutoDetect._
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.annotate._
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion._

@JsonAutoDetect(value = Array(JsonMethod.FIELD), fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
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
  val deals: java.util.List[Int] = null,
  val shop_ids: java.util.List[Int] = null
  ) {
  def this() = this(0, 0, 0, 0, 0, 0, 0, 0,0,null, null)
}
