package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.cache._
import com.hui800.listing.svc._
import com.hui800.listing.db.model._
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import scala.annotation.tailrec
import com.hui800.listing.util.FutureUtils._

class MallController(
  service:         MallService,
  defaultPageSize: Int,
  maxPageSize:     Int
) extends AbstractController("/mall/") {

  val logger = Logger(getClass.getName)

  def respond(request: HttpRequest): Future[Any] = {
    request.path.substring(prefix.length) match {
      case "shops.json" => futurePool(brandsInMall(request.params))
      case "dealShops.json" => futurePool(shopsForDeal(request.params))
      case _ => throw HttpException.RESOURCE_NOT_FOUND
    }
  }

  def brandsInMall(params: Parameters) = {

    val mallId    = params.required("mall", _.toInt)
    val floor     = params.optional("floor", _.toInt)
    val subcate   = params.optional("subcate")
    val letter    = params.optional("letter")
    val userShops = params.optionalList("userShops", _.toInt, ",").getOrElse(Nil).toSet
    val now       = params.optional("now", _.toLong).getOrElse(System.currentTimeMillis)
    val pageNo    = params.optional("p", _.toInt).getOrElse(1)
    val pageSize  = math.min(
      maxPageSize,
      params.optional("ps", _.toInt).getOrElse(defaultPageSize)
    )

    inTransaction {

      Session.currentSession.setLogger(Logger("squeryl").debug(_))

      val brandsInMall = service.brandsInMall(mallId, floor, subcate, letter, now)
      // 有deal的商户,按照第一个有效deal的优先级进行分组;没有有效deal的商户单独分一组
      val (activeBrands, inactiveBrands) = brandsInMall.partition(s => s.deals.nonEmpty)
      val brandsByPriority = activeBrands.groupBy(_.deals(0).priority) + (3 -> inactiveBrands)
      // 同一优先级的商户，按照用户关注品牌在前，非关注品牌在后；brand_rank从高到低的顺序排序
      val brandsSorted = brandsByPriority map {
        case (priority, shops) =>
          val (user, nonUser) = shops.partition(s => userShops.contains(s.shopId))
          (priority, user.sortBy(_.brandRank).reverse ++ nonUser.sortBy(_.brandRank).reverse)
      }
      // 按照优先级从小到大的顺序排序
      val shops = (1 to 3).flatMap(brandsSorted.getOrElse(_, Nil)).toList
      // 取当前分页
      val page = shops.slice((pageNo - 1) * pageSize, pageNo * pageSize)

      Map(
        "stats" -> Map(
          "now"     -> now,
          "floor"   -> service.countByFloor(service.brandsInMall(mallId, None, subcate, letter, now)),
          "subcate" -> service.countBySubcate(service.brandsInMall(mallId, floor, None, letter, now)),
          "letter"  -> service.countByLetter(service.brandsInMall(mallId, floor, subcate, None, now))
        ),
        "page"  -> Map(
          "size"  -> shops.size,
          "p"     -> pageNo,
          "ps"    -> pageSize,
          "items" -> page.map(shop => Map(
              "shop_id"  -> shop.shopId,
              "brand_id" -> shop.brandId,
              "brand_rank" -> shop.brandRank,
              "priority" -> shop.deals.headOption.map(_.priority).getOrElse(3),
              "deals"    -> shop.deals.slice(0, 1).map(_.id)
            )
          )
        )
      )
    }
  }

  def shopsForDeal(params: Parameters) = {

    val dealId = params.required("deal", _.toInt)
    val now    = params.optional("now", _.toLong).getOrElse(System.currentTimeMillis)

    @tailrec
    def floorList(
      list:  List[(Int, List[MallShopDeal])],
      shops: List[MallShopDeal]
    ): List[(Int, List[MallShopDeal])] = {
      shops match {
        case Nil =>
          list
        case _ =>
          val floorId = shops.head.floorId
          val (shopsOnThisFloor, others) = shops.partition(_.floorId == floorId)
          floorList((floorId, shopsOnThisFloor) :: list, others)
      }
    }

    val shops = inTransaction {
      Session.currentSession.setLogger(Logger("squeryl").debug(_))
      service.shopsForDeal(dealId, now)
    }
    floorList(Nil, shops) map {
      case (floorId, shops) =>
        Map(
          "floor_id" -> floorId,
          "shops"    -> shops.map(s => Map(
              "shop_id"  -> s.shopId,
              "brand_id" -> s.brandId,
              "deals"    -> s.deals.slice(0, 1).map(_.id)
            )
          )
        )
    }
  }
}
