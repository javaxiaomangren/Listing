package com.hui800.listing.svc.model

import org.codehaus.jackson.annotate.{JsonMethod, JsonAutoDetect, JsonCreator}
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility._
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion._


/**
 * Created by IntelliJ IDEA.
 * User: zhang
 * Date: 12-6-21
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect(value = Array(JsonMethod.FIELD), fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
case class MidItem @JsonCreator()(
  id: Any,
  count: Long,
  children: Array[MidItem] = null
  ) {
  def this() = this(null, 0)
}
