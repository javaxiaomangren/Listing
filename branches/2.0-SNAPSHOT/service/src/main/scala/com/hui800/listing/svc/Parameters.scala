package com.hui800.listing.svc

import com.hui800.listing.db.Pager
import com.hui800.listing.cache.CacheKey
import scala.collection.Map
import scala.math._

case class Parameters(
  params: Map[String, List[String]],
  maxPageSize: Int,
  defaultPageSize: Int
) extends CacheKey {

  lazy val categoryId = param("category", "行业类别")
  lazy val subCateId  = param("subcategory", "行业类别")
  lazy val cityId     = param("city", "城市")(_.toInt)
  lazy val districtId = param("district", "区县")(_.toInt)
  lazy val bizoneId   = param("bizone", "商圈")(_.toInt)
  lazy val mallId     = param("mall", "商场")(_.toInt)
  lazy val bankId     = param("bank", "银行")(_.split(",").map(_.toInt)).flatten.toList
  lazy val dealType   = param("deal_type", "优惠类别")
  lazy val subType    = param("sub_type", "优惠子类别").toList
  lazy val dealBrand  = param("brand_id", "deal品牌")(_.toInt)
  lazy val shopBrand  = param("shop_brand", "商户品牌")(_.toInt)
  lazy val dealLimit  = param("count_per_group", "最多返回的deal数",default = Some(3))(_.toInt)
  lazy val sortKey    = param("sort", "排序方式", default = Some("score"))
  lazy val ascending  = param("asc", "是否升序排列", default = Some(false))(_.toBoolean).get
  lazy val pageNo     = param("page", "页号", default = Some(1))(_.toInt).get
  lazy val pageSize   = param("per_page", "页长", default = Some(defaultPageSize))(ps => min(ps.toInt, maxPageSize)).get
  lazy val debug      = param("debug", "是否为调试模式", default = Some(false))(_.toBoolean).get
  lazy val letter     = param("letter", "首字母")(_.trim.substring(0, 1))
  lazy val opTag      = param("operate_tag", "deal标志位")(_.toInt)
  lazy val content    = param("content", "聚合方式", default = Some("brand")).get
  lazy val brandTag   = param("brand_tag", "风格")(_.toInt)
  lazy val pager      = Pager((pageNo - 1) * pageSize, pageSize)
  lazy val dataSource = param("data_source", "", optional = false).get

  private def param[T]
  (name: String, cnName: String, optional: Boolean = true, default: Option[T] = None)
  (implicit conv: String => T = (value: String) => value) = {
    (params.get(name), optional) match {
      case (Some(List(value: String, _*)), _) if value.trim.nonEmpty =>
        try {
          Some(conv(value))
        } catch {
          case ex: NumberFormatException =>
            throw new IllegalArgumentException(
              String.format("非法的参数值: %s=%s", name, value))
        }
      case (_, true) =>
        default
      case _ =>
        throw new IllegalArgumentException(
          String.format("请指定%s(%s)", cnName, name))
    }
  }
  
  def toCacheKey = {
    val sb = new StringBuilder("{")
    params foreach {
      case (key, value) =>
        if (key != "page" && key != "per_page") {
          val v = value.filterNot(_.isEmpty)
          if (v.nonEmpty) {
            sb.append(key).append(":").append(v.mkString("|")).append(",")
          }
        }
    }
    sb.append("p:").append(pageNo).append(",")
    sb.append("ps:").append(pageSize)
    sb.append("}").toString
  }
  
  override def toString = toCacheKey
}
