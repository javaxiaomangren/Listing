package com.hui800.listing.svc.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.svc._

class MallServiceImpl(queries: MallQueries) extends MallService {

  def shopsInMall(
    mallId:  Int,
    floor:   Option[Int],
    subcate: Option[String],
    letter:  Option[String],
    now:     Long
  ) = {
    queries.getShopList(mallId, now)
    // 根据筛选条件过滤
    .filter(s =>
      floor.map(_ == s.floorId).getOrElse(true) &&
      subcate.map(s.subcateList.contains(_)).getOrElse(true) &&
      letter.map(_.contains(s.pinyin(0))).getOrElse(true)
    )
    // 过滤掉无效的deal
    .map(
      s => s.copy(
        deals = s.deals.filter(d => d.fromTime <= now && (d.toTime < 0 || d.toTime > now))
      )
    )
  }

  def brandsInMall(
    mallId:  Int,
    floor:   Option[Int],
    subcate: Option[String],
    letter:  Option[String],
    now:     Long
  ) = {
    queries.getBrandList(mallId, floor, now)
    // 根据筛选条件过滤
    .filter({s =>
      subcate.map(s.subcateList.contains(_)).getOrElse(true) &&
      letter.map(_.contains(s.pinyin(0))).getOrElse(true)
    })
    // 过滤掉无效的deal
    .map(
      s => s.copy(
        deals = s.deals.filter(d => d.fromTime <= now && (d.toTime < 0 || d.toTime > now))
      )
    )
  }

  def shopsForDeal(
    dealId: Int,
    now:    Long
  ): List[MallShopDeal] = {
    queries.getShopListForDeal(dealId)
    // 过滤掉无效的deal
    .map(
      s => s.copy(
        //deals = s.deals.filter(d => d.fromTime <= now && (d.toTime < 0 || d.toTime > now))
        deals = s.deals.slice(0, 1)
      )
    )
  }

  def countByFloor(shops: List[MallShopDeal]) = {
    Map("total" -> shops.count(_.deals.nonEmpty)) ++
    // 按楼层分组
    shops.groupBy(_.floorId)
    // 计算活跃商户数
    .map(
      kv => (kv._1 -> kv._2.filter(_.deals.nonEmpty).size)
    )
  }

  def countBySubcate(shops: List[MallShopDeal]) = {
    Map("total" -> shops.count(_.deals.nonEmpty)) ++
    // 展开子分类列表
    shops.flatMap(shop => shop.subcateList.map((_ -> shop)))
    // 按子分类分组
    .groupBy(_._1)
    // 计算活跃商户数
    .map(
      kv => (kv._1 -> kv._2.map(_._2).filter(_.deals.nonEmpty).size)
    )
  }

  def countByLetter(shops: List[MallShopDeal]) = {
    Map("total" -> shops.count(_.deals.nonEmpty)) ++
    // 按首字母分组
    shops.groupBy(_.pinyin.substring(0, 1))
    // 计算分组的大小
    .map(group => (group._1, group._2.filter(_.deals.nonEmpty).size))
  }
}
