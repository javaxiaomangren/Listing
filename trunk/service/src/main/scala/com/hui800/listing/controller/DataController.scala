package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.svc.{BankService, ListService}
import com.hui800.listing.util.FutureUtils
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import com.hui800.listing.cache._
import com.hui800.listing.cache.Cacheable._
import scala.collection.JavaConversions._

abstract class DataController(
  listService: ListService,
  bankService: BankService,
  defaultPageSize: Int,
  maxPageSize: Int
) extends AbstractController("/data/") with Memo with CacheComponent {

  val logger = Logger(getClass.getName)
  
  lazy val _list = staleOkMemoize("ctrl:list", list _)
  lazy val _dp = staleOkMemoize("ctrl:dp", dp _)
  lazy val _dealCounts = staleOkMemoize("ctrl:dlCnt", dealCounts _)
  
  def respond(request: HttpRequest): Future[Any] = {
    val decoder = new QueryStringDecoder(request.uri)
    val params = new Parameters(
      decoder.getParameters.map(entry => (entry._1, entry._2.toList)).filter(_._2.filter(_.trim.nonEmpty).nonEmpty).toMap
    ) with CacheKey {
      
      def toCacheKey: String = {
        toString(this.params)
      }
      
      private def toString(obj: Any): String = {
        obj match {
          case map: Map[_, _] =>
            map
            .map(kv => (toString(kv._1), toString(kv._2)))
            .toList
            .sortBy(_._1)
            .map(kv => "%s:%s".format(kv._1, kv._2))
            .mkString("{", ",", "}")
          case list: List[_] =>
            if (list.size > 1)
              list.map(e => toString(e)).mkString("[", ",", "]")
            else
              list.map(e => toString(e)).mkString
          case key: CacheKey =>
            key.toCacheKey
          case _ =>
            obj.toString
        }
      }
    }
    request.path.substring(prefix.length) match {
      case "list.json" => FutureUtils.futurePool(_list(params, defaultPageSize, maxPageSize))
      case "DP.json" => FutureUtils.futurePool(_dp(params, defaultPageSize, maxPageSize))
      case "bank/dealCounts.json" => FutureUtils.futurePool(_dealCounts(params))
      case _ => throw HttpException.RESOURCE_NOT_FOUND
    }
  }
  
  def list(params: Parameters, dps: Int, mps: Int) = {
    FreshValue(listService.list(CommonParameters(params, dps, mps)).toMap)
  }
  
  def dp(params: Parameters, dps: Int, mps: Int) = {
    FreshValue(listService.shopIdList(CommonParameters(params, dps, mps)).toMap)
  }
  
  def dealCounts(params: Parameters) = {
    FreshValue(bankService.dealCounts(params))
  }
}
