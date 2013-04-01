package com.hui800.listing.svc.impl

import com.hui800.listing.svc._
import com.hui800.listing.svc.model._
import com.hui800.listing.db._

class DealCountServiceImpl(dealQueries: DealQueries) extends CountService {

  def countByCategory(params: QueryParams, children: Option[(String, List[MidItem])] = None): List[MidItem] = {
    MidItem("total", dealQueries.countDeals(params)) ::
    dealQueries.countDealsGroupByCategory(params).map(
      item => MidItem(item._1, item._2, children match {
          case Some((item._1, children)) => children.toArray
          case _ => null
        })
    )
  }

  def countBySubCate(params: QueryParams): List[MidItem] = {
    dealQueries.countDealsGroupBySubCate(params).map(item => MidItem(item._1, item._2))
  }

  def countByDistrict(params: QueryParams, children: Option[(Int, List[MidItem])] = None): List[MidItem] = {
    MidItem("total", dealQueries.countDeals(params)) ::
    dealQueries.countDealsGroupByDistrict(params).map(
      item => MidItem(item._1, item._2, children match {
          case Some((item._1, children)) => children.toArray
          case _ => null
        })
    )
  }

  def countByBizone(params: QueryParams): List[MidItem] = {
    dealQueries.countDealsGroupByBizone(params).map(item => MidItem(item._1, item._2))
  }

  def countByDealType(
    params: QueryParams, children: Option[(String, List[MidItem])] = None
  ): List[MidItem] = {
    dealQueries.countDealsGroupByDealType(params).map(
      item => MidItem(item._1, item._2, children match {
          case Some((item._1, children)) => children.toArray
          case _ => null
        })
    )
  }

  def countBySubType(params: QueryParams): List[MidItem] = {
    dealQueries.countDealsGroupBySubType(params).map(item => MidItem(item._1, item._2))
  }

  def countByBrand(params: QueryParams): List[MidItem] = {
    dealQueries.countDealsGroupByBrand(params).map(item => MidItem(item._1, item._2))
  }

  def countByBank(params: QueryParams): List[MidItem] = {
    dealQueries.countDealsGroupByBank(params).map(item => MidItem(item._1, item._2))
  }
}
