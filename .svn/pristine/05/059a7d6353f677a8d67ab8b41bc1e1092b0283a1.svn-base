package com.hui800.listing.svc.model

import org.codehaus.jackson.annotate._
import org.codehaus.jackson.annotate.JsonAutoDetect._
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.annotate._
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion._

@JsonAutoDetect(value = Array(JsonMethod.FIELD), fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
case class Result @JsonCreator() (
  page: Int,
  count: Int,
  items: java.util.List[Any]
) {
  def this() = this(0, 0, null)
}
