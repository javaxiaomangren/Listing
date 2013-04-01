package com.hui800.listing.svc

import com.hui800.listing.svc.model._
import com.hui800.listing.db._

trait CountService {

  def countByCategory(params: QueryParams, children: Option[(String, List[MidItem])] = None): List[MidItem]

  def countBySubCate(params: QueryParams): List[MidItem]

  def countByDistrict(params: QueryParams, children: Option[(Int, List[MidItem])] = None): List[MidItem]

  def countByBizone(params: QueryParams): List[MidItem]

  def countByDealType(
    params: QueryParams, children: Option[(String, List[MidItem])] = None
  ): List[MidItem]

  def countBySubType(params: QueryParams): List[MidItem]

  def countByBrand(params: QueryParams): List[MidItem]

  def countByBank(params: QueryParams): List[MidItem]
}
