package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.util.Pager

@deprecated
case class CommonParameters(params: Parameters, defaultPageSize: Int, maxPageSize: Int) {

  lazy val categoryId = params.optional("category")
  lazy val subCateId  = params.optional("subcategory")
  lazy val cityId     = params.optional("city", _.toInt)
  lazy val districtId = params.optional("district", _.toInt)
  lazy val bizoneId   = params.optional("bizone", _.toInt)
  lazy val mallId     = params.optional("mall", _.toInt)
  lazy val bankId     = params.optionalList("bank", _.toInt, ",").getOrElse(Nil)
  lazy val dealType   = params.optional("deal_type")
  lazy val subType    = params.optionalList("sub_type").getOrElse(Nil)
  lazy val dealBrand  = params.optional("brand_id", _.toInt)
  lazy val shopBrand  = params.optional("shop_brand", _.toInt)
  lazy val dealLimit  = params.optional("count_per_group", _.toInt).getOrElse(3)
  lazy val sortKey    = params.optional("sort").getOrElse("score")
  lazy val debug      = params.optional("debug", _.toBoolean).getOrElse(false)
  lazy val ascending  = params.optional("asc", _.toBoolean).getOrElse(false)
  lazy val pageNo     = params.optional("page", _.toInt).getOrElse(1)
  lazy val pageSize   = math.min(maxPageSize, params.optional("per_page", _.toInt).getOrElse(defaultPageSize))
  lazy val letter     = params.optional("letter", _.trim.substring(0, 1))
  lazy val opTag      = params.optional("operate_tag", _.toInt)
  lazy val content    = params.optional("content").getOrElse("brand")
  lazy val brandTag   = params.optional("brand_tag", _.toInt)
  lazy val pager      = Pager((pageNo - 1) * pageSize, pageSize)
  lazy val dataSource = params.required("data_source")
}
