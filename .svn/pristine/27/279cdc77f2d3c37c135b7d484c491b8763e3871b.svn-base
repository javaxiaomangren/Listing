package com.hui800.listing.svc.model

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonAutoDetect._
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility._
import com.fasterxml.jackson.annotation.JsonInclude._
import com.fasterxml.jackson.databind.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize._
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion._

/**
 * Created by IntelliJ IDEA.
 * User: zhang
 * Date: 12-6-21
 * Time: 下午5:53
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect(fieldVisibility = ANY)
@JsonSerialize(include = NON_DEFAULT)
@JsonInclude(Include.NON_NULL)
@deprecated
case class FinalResult @JsonCreator() (
  list: Result,
  cate_nav: Array[Map[String, _]],
  loc_nav: Array[Map[String, _]],
  type_nav: Array[Map[String, _]],
  bank_nav: Array[Map[String, _]],
  brand_nav: Array[Map[String, _]],
  mall_nav: Array[Map[String, _]]
) {
  def this() = this(null, null, null, null, null, null,null)
  def toMap = Map(
    "list" -> list,
    "cate_nav" -> cate_nav,
    "loc_nav" -> loc_nav,
    "type_nav" -> type_nav,
    "bank_nav" -> bank_nav,
    "brand_nav" -> brand_nav,
    "mall_nav" -> mall_nav
  ).filterNot(_._2 == null)
}
