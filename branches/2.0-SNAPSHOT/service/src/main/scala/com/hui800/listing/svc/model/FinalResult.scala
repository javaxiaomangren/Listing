package com.hui800.listing.svc.model

import org.codehaus.jackson.annotate.JsonAutoDetect._
import scala.Array._
import org.codehaus.jackson.annotate.{JsonMethod, JsonAutoDetect, JsonCreator}
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility._
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.codehaus.jackson.map.annotate.JsonSerialize._
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion._


/**
 * Created by IntelliJ IDEA.
 * User: zhang
 * Date: 12-6-21
 * Time: 下午5:53
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect(value = Array(JsonMethod.FIELD), fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
case class FinalResult @JsonCreator()
(
  list: Result,
  cate_nav: Array[MidItem],
  loc_nav: Array[MidItem],
  type_nav: Array[MidItem],
  bank_nav: Array[MidItem],
  brand_nav: Array[MidItem],
  mall_nav: Array[MidItem]
  ) {
  def this() = this(null, null, null, null, null, null,null)
}
