package com.hui800.listing.svc.model

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility._
import com.fasterxml.jackson.annotation.JsonInclude._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion._


/**
 * Created by IntelliJ IDEA.
 * User: zhang
 * Date: 12-6-21
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect(fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
@JsonInclude(Include.NON_NULL)
@deprecated
case class MidItem @JsonCreator()(
  id: Any,
  count: Long,
  children: Array[Map[String, _]] = null
) {
  def this() = this(null, 0)
  def toMap = Map(
    "id" -> id,
    "count" -> count,
    "children" -> children
  ).filterNot(_._2 == null)
}
