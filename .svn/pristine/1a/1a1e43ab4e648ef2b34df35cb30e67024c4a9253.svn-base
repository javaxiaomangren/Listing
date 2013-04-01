package com.hui800.listing.db.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.util.SquerylUtils._
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

class MallQueriesImpl(db: DB) extends MallQueries {

  def getShopList(
    mallId:  Int,
    now: Long
  ) = {
    from(db.mallShopDeals)(msd =>
      where(
        msd.mallId === mallId and
        (msd.dealId === 0 or msd.toTime.isNull or msd.toTime > now)
      )
      groupBy(
        msd.shopId
      )
      compute(
        msd.floorId,
        msd.subcates,
        msd.brandId,
        msd.brandRank,
        msd.pinyin,
        groupConcat(
          expr    = List(
            msd.dealId.toString,
            ",",
            msd.priority.toString,
            ",",
            msd.fromTime.toString,
            ",",
            msd.toTime.getOrElse(-1).toString
          ),
          orderBy = List(
            msd.priority   asc,
            msd.createTime desc
          ),
          separator = ","
        )
      )
    ).toList.map(msd =>
      MallShopDeal(
        shopId    = msd.key,
        floorId   = msd.measures._1,
        subcates  = msd.measures._2,
        brandId   = msd.measures._3,
        brandRank = msd.measures._4,
        pinyin    = msd.measures._5,
        deals     = Option(msd.measures._6).map(
          _.split(",").filter(_.nonEmpty).toList.map(_.toLong).grouped(4).toList
        ).getOrElse(Nil) map {
          case List(dealId, priority, fromTime, toTime) =>
            MallDeal(dealId.toInt, priority.toInt, fromTime, toTime)
          case _ =>
            throw new RuntimeException("impossible")
        }
      )
    )
  }

  def getBrandList(
    mallId:  Int,
    floor: Option[Int],
    now: Long
  ) = {
    from(db.mallShopDeals)(msd =>
      where(
        msd.mallId === mallId and
        msd.floorId === floor.? and
        (msd.dealId === 0 or msd.toTime.isNull or msd.toTime > now)
      )
      groupBy(
        msd.brandId
      )
      compute(
        msd.shopId,
        msd.subcates,
        msd.brandRank,
        msd.pinyin,
        groupConcat(
          expr    = List(
            msd.dealId.toString,
            ",",
            msd.priority.toString,
            ",",
            msd.fromTime.toString,
            ",",
            msd.toTime.getOrElse(-1).toString
          ),
          orderBy = List(
            msd.priority   asc,
            msd.createTime desc
          ),
          separator = ","
        )
      )
    ).toList.map(msd =>
      MallShopDeal(
        shopId    = msd.measures._1,
        subcates  = msd.measures._2,
        brandId   = msd.key,
        brandRank = msd.measures._3,
        pinyin    = msd.measures._4,
        deals     = Option(msd.measures._5).map(
          _.split(",").filter(_.nonEmpty).toList.map(_.toLong).grouped(4).toList
        ).getOrElse(Nil) map {
          case List(dealId, priority, fromTime, toTime) =>
            MallDeal(dealId.toInt, priority.toInt, fromTime, toTime)
          case _ =>
            throw new RuntimeException("impossible")
        }
      )
    )
  }

  def getShopListForDeal(dealId: Int) = {
    from(db.mallShopDeals)(msd =>
      where(msd.mallDealId === dealId)
      groupBy(msd.shopId)
      compute(
        msd.floorId,
        msd.brandId,
        groupConcat(
          expr    = List(
            msd.dealId.toString,
            ",",
            msd.priority.toString,
            ",",
            msd.fromTime.toString,
            ",",
            msd.toTime.getOrElse(-1).toString
          ),
          orderBy = List(
            msd.priority   asc,
            msd.createTime desc
          ),
          separator = ","
        )
      )
      orderBy(
        msd.floorOrder asc,
        msd.brandRank  desc
      )
    ).toList.map(msd =>
      MallShopDeal(
        shopId     = msd.key,
        floorId    = msd.measures._1,
        brandId    = msd.measures._2,
        deals      = Option(msd.measures._3).map(
          _.split(",").filter(_.nonEmpty).toList.map(_.toLong).grouped(4).toList
        ).getOrElse(Nil) map {
          case List(dealId, priority, fromTime, toTime) =>
            MallDeal(dealId.toInt, priority.toInt, fromTime, toTime)
          case _ =>
            throw new RuntimeException("impossible")
        }
      )
    )
  }
}
