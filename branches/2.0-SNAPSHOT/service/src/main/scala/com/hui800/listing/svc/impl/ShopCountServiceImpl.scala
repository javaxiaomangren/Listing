package com.hui800.listing.svc.impl

import com.hui800.listing.svc._
import com.hui800.listing.svc.model._
import com.hui800.listing.db._

class ShopCountServiceImpl(
  shopQueries: ShopQueries,
  dealQueries: DealQueries
) extends CountService {

  def countByCategory(params: QueryParams, children: Option[(String, List[MidItem])] = None): List[MidItem] = {
    MidItem("total", shopQueries.countShops(params)) ::
    shopQueries.countShopsGroupByCategory(params).map(
      item => MidItem(item._1, item._2, children match {
          case Some((item._1, children)) => children.toArray
          case _ => null
        })
    )
  }

  def countBySubCate(params: QueryParams): List[MidItem] = {
    shopQueries.countShopsGroupBySubCate(params).map(item => MidItem(item._1, item._2))
  }

  def countByDistrict(params: QueryParams, children: Option[(Int, List[MidItem])] = None): List[MidItem] = {
    MidItem("total", shopQueries.countShops(params)) ::
    shopQueries.countShopsGroupByDistrict(params).map(
      item => MidItem(item._1, item._2, children match {
          case Some((item._1, children)) => children.toArray
          case _ => null
        })
    )
  }

  def countByBizone(params: QueryParams): List[MidItem] = {
    shopQueries.countShopsGroupByBizone(params).map(item => MidItem(item._1, item._2))
  }

  def countByDealType(
    params: QueryParams, children: Option[(String, List[MidItem])] = None
  ): List[MidItem] = {
    shopQueries.countShopsGroupByDealType(params).map(
      item => (item._1, children) match {
        case ("30000000", Some(("30000000", children))) =>
          MidItem(item._1, children.map(_.count).sum, children.toArray)
        case ("30000000", _) =>
          MidItem(item._1, countBySubType(params.copy(dealType = Some("30000000"))).map(_.count).sum)
        case (_, Some((item._1, children))) => 
          MidItem(item._1, item._2, children.toArray)
        case _ =>
          MidItem(item._1, item._2)
      }
    )
  }

  def countBySubType(params: QueryParams): List[MidItem] = {
    val shopCounts = shopQueries.countShopsGroupBySubType(params)
    val allCounts = params.dealType match {
      case Some("30000000") =>
        val dealIds = dealQueries.getDealIds(
          params.copy(subType = List("30000001", "30000004", "30000005"))
        )
        val subTypeAndBankIds = dealQueries.getSubTypeAndBankIds(dealIds: _*).map(d => (d._2, d._3)).toSet
        val bankCounts = subTypeAndBankIds.groupBy(_._1).map(e => (e._1, e._2.size.toLong)).toList
        shopCounts ++ bankCounts
      case _ =>
        shopCounts
    }
    allCounts.map(item => MidItem(item._1, item._2))
  }

  def countByBrand(params: QueryParams): List[MidItem] = {
    shopQueries.countShopsGroupByBrand(params).map(item => MidItem(item._1, item._2))
  }

  def countByBank(params: QueryParams): List[MidItem] = {
    shopQueries.countShopsGroupByBank(params).map(item => MidItem(item._1, item._2))
  }
}
