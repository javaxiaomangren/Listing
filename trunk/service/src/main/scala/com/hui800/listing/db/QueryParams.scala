package com.hui800.listing.db

import com.hui800.listing.util._
import com.hui800.listing.cache._

case class QueryParams(
  val cityId:      Int            = 110000,
  val districtId:  Option[Int]    = None,
  val bizoneId:    Option[Int]    = None,
  val categoryId:  Option[String] = None,
  val subCateId:   Option[String] = None,
  val mallId:      Option[Int]    = None,
  val dealBrandId: Option[Int]    = None,
  val shopBrandId: Option[Int]    = None,
  val shopId:      Option[Int]    = None,
  val bankId:      List[Int]      = Nil,
  val dealType:    Option[String] = None,
  val subType:     List[String]   = Nil,
  val pager:       Option[Pager]  = None,
  val opTag:       Option[Int]    = None,
  val brandFL:     Option[String] = None,
  val brandTag:    Option[Int]    = None,
  val noExpDeals:  Boolean        = true
) extends CacheKey {
  
  def toCacheKey = {
    val sb = new StringBuilder("{")
    getClass.getDeclaredFields.map { field =>
      field.setAccessible(true)
      field.get(this) match {
        case Some(x) => sb.append(field.getName).append(":").append(x).append(",")
        case None =>
        case x: List[_] => sb.append(field.getName).append(":").append(x.mkString("|")).append(",")
        case x => sb.append(field.getName).append(":").append(x).append(",")
      }
    }
    if (sb.length > 1) {
      sb.length = sb.length - 1
    }
    sb.append("}").toString
  }
  
  override def toString = toCacheKey
}
