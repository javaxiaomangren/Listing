package com.hui800.listing.svc.model

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonAutoDetect._
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility._
import com.fasterxml.jackson.annotation.JsonInclude._
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion._

@JsonAutoDetect(fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
@JsonInclude(Include.NON_NULL)
@deprecated
case class Result @JsonCreator() (
  page: Int,
  count: Int,
  items: List[Any]
) {
  def this() = this(0, 0, null)
  def toMap = Map(
    "page" -> page,
    "count" -> count,
    "items" -> items
  ).filterNot(_._2 == null)
}
