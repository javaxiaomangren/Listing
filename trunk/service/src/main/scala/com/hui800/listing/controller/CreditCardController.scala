package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.svc.CreditCardService
import com.hui800.listing.util._
import com.hui800.listing.util.FutureUtils._
import com.hui800.listing.cache._
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session

class CreditCardController(
  service:         CreditCardService,
  defaultPageSize: Int,
  maxPageSize:     Int
) extends AbstractController("/creditcard/") {

  val logger = Logger(getClass.getName)
  
  def respond(request: HttpRequest): Future[Any] = {
    request.path.substring(prefix.length) match {
      case "deals.json" => futurePool(dealList(request.params))
      case "shops.json" => futurePool(brandOrShopList(request.params))
      case _ => throw HttpException.RESOURCE_NOT_FOUND
    }
  }
  
  def dealList(params: Parameters) = {
    
    val cityCode = params.required("city")
    val bankId   = params.required("bank", _.toInt)
    val typeCode = params.required("type")
    val now      = params.optional("now", _.toLong).getOrElse(System.currentTimeMillis)
    val excludes = params.optionalList("excludes", _.toInt, ",").getOrElse(Nil).toSet
    val pageNo   = params.optional("p", _.toInt).getOrElse(1)
    val pageSize = math.min(
      maxPageSize, 
      params.optional("ps", _.toInt).getOrElse(defaultPageSize)
    )
    
    inTransaction {
    
      Session.currentSession.setLogger(Logger("squeryl").debug(_))
    
      val pageOffset = excludes.size / pageSize
      val offset = excludes.size % pageSize
      val pager = Pager((pageNo - pageOffset - 1) * pageSize - offset, pageSize)
      val now = System.currentTimeMillis
      val (count, deals) = service.listCardDeals(
        cityCode, bankId, typeCode, now, excludes, pager
      )
      val totalCount = getTotalCount(cityCode, bankId)
      val countMap = service.countCardDeals(cityCode, bankId)//.filter(_._1.startsWith(typeCode))
    
      Map(
        "stats" -> (totalCount ++ Map(
            "type" -> countMap
          )),
        "page" -> Map(
          "size"  -> count,
          "p"     -> pageNo,
          "ps"    -> pageSize,
          "items" -> deals.map(_.id)
        )
      )
    }
  }
  
  def brandOrShopList(params: Parameters) = {
    
    val cityCode = params.required("city")
    val bankId   = params.required("bank", _.toInt)
    val category = params.optional("category")
    val subcate  = params.optional("subcate")
    val now      = params.optional("now", _.toLong).getOrElse(System.currentTimeMillis)
    val pageNo   = params.optional("p", _.toInt).getOrElse(1)
    val pageSize = math.min(
      maxPageSize, 
      params.optional("ps", _.toInt).getOrElse(defaultPageSize)
    )
    
    inTransaction {
    
      Session.currentSession.setLogger(Logger("squeryl").debug(_))
    
      val pager = Pager((pageNo - 1) * pageSize, pageSize)
      val now = System.currentTimeMillis
    
      val (count, boses) = service.listCardBrandOrShop(cityCode, bankId, category, subcate, now, pager)
      val totalCount = getTotalCount(cityCode, bankId)
      val countByCat1 = service.countBrandOrShopsByCategory(cityCode, bankId, 1);
      val countByCat2 = service.countBrandOrShopsByCategory(cityCode, bankId, 2);
    
      Map(
        "stats" -> (totalCount ++ Map(
            "category" -> countByCat1,
            "subcate" -> countByCat2
          )),
        "page" -> Map(
          "size"  -> count,
          "p"     -> pageNo,
          "ps"    -> pageSize,
          "items" -> boses.map {
            bos => Map(
              "type"  -> bos.typeCode,
              "id"    -> bos.id,
              "deals" -> bos.dealList.slice(0, 3).map(a => Map(
                  "id"         -> a._1,
                  "shop_count" -> a._2
                )
              )
            )
          }
        )
      )
    }
  }
  
  private def getTotalCount(cityCode: String, bankId: Int) = {
    Map(
      "deal_count" -> service.countCardDeals(cityCode, bankId).filterKeys(_.length == 2),
      "shop_count" -> service.countBrandOrShopsByCategory(cityCode, bankId, 1).values.sum
    )
  }
}
