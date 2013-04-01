package com.hui800.listing

import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import com.twitter.finagle.stats.OstrichStatsReceiver
import com.twitter.finagle.{SimpleFilter, Filter}
import com.twitter.logging.Logger
import com.twitter.ostrich.admin._
import com.twitter.util.Future
import com.twitter.util.FuturePool
import java.net.InetSocketAddress
import javax.sql.DataSource
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.util.CharsetUtil._
import org.squeryl.adapters.MySQLAdapter
import scala.collection.JavaConversions._
import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.cache._
import com.hui800.listing.db.impl._
import com.hui800.listing.svc._
import com.hui800.listing.svc.cache._
import com.hui800.listing.svc.impl._
import com.hui800.listing.util._


class ListingService(
  val machineId: Short,
  val port: Int,
  val adminPort: Int,
  val dataSource: DataSource,
  val maxPageSize: Int,
  val defaultPageSize: Int,
  val cache: Option[Cache],
  futurePool: FuturePool
) extends Service {
  
  import config._
  
  val log = Logger.get(getClass.getName)
  private var server: Server = null

  def start: Unit = {
    val db = new DB(dataSource, new MySQLAdapter)
    val (dealQueries, shopQueries, regionQueries) = cache match {
      case Some(cacheInstance) =>
        trait CacheComponentImpl extends CacheComponent {
          val cache = cacheInstance
        }
        (
          new DealQueriesImpl(db) with DealQueriesCache with CacheComponentImpl,
          new ShopQueriesImpl(db) with ShopQueriesCache with CacheComponentImpl,
          new RegionQueriesImpl(db) with RegionQueriesCache with CacheComponentImpl
        )
      case None => (
          new DealQueriesImpl(db),
          new ShopQueriesImpl(db),
          new RegionQueriesImpl(db)
        )
    }
    val dealCountService = new DealCountServiceImpl(dealQueries)
    val shopCountService = new ShopCountServiceImpl(shopQueries, dealQueries)
    val regionService = new RegionServiceImpl(regionQueries)
    val bankService = new BankServiceImpl(dealCountService)
    val listService = cache match {
      case Some(cacheInstance) =>
        trait CacheComponentImpl extends CacheComponent {
          val cache = cacheInstance
        }
        new ListServiceImpl(
          dealQueries = dealQueries,
          shopQueries = shopQueries,
          regionService = regionService,
          shopCountService = shopCountService,
          dealCountService = dealCountService
        ) with ListServiceCache with CacheComponentImpl
      case None =>
        new ListServiceImpl(
          dealQueries = dealQueries,
          shopQueries = shopQueries,
          regionService = regionService,
          shopCountService = shopCountService,
          dealCountService = dealCountService
        )
    }
    val service = accessLog andThen errHandler andThen logic(listService, bankService)
    server = ServerBuilder()
    .codec(Http())
    .bindTo(new InetSocketAddress(port))
    .name("httpserver")
    .reportTo(new OstrichStatsReceiver)
    .build(service)
    log.info("HTTP interface started on port %d.", port)
  }
  
  def shutdown = {
    server.close()
  }
  
  val accessLog = new Filter[HttpRequest, HttpResponse, (ID, HttpRequest), HttpResponse] {
    def apply(req: HttpRequest, svc: HttpService) = {
      val id = ID.generate(machineId)
      log.info("%s %s %s %s", id, req.getMethod, req.getUri, req.getProtocolVersion)
      svc((id, req)) map { resp =>
        val status = resp.getStatus
        val delta = System.currentTimeMillis - id.timestamp
        log.info("%s %s %s %s %s %s", id, req.getMethod, req.getUri, req.getProtocolVersion, status.getCode, delta)
        resp
      }
    }
  }
  
  val errHandler = new SimpleFilter[(ID, HttpRequest), HttpResponse] {
    def apply(request: (ID, HttpRequest), service: HttpService) = {
      service(request) handle { 
        case ex: IllegalArgumentException =>
          val errorResponse = new DefaultHttpResponse(HTTP_1_1, BAD_REQUEST)
          errorResponse.setContent(copiedBuffer(ex.getMessage, UTF_8))
          errorResponse.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
          errorResponse
        case ex: HttpException =>
          val errorResponse = new DefaultHttpResponse(HTTP_1_1, ex.status)
          errorResponse.setContent(copiedBuffer(ex.errMsg, UTF_8))
          errorResponse.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
          errorResponse
        case ex =>
          val errMsg = String.format(
            "%s: %s\n%s", ex.getClass.getName, ex.getMessage,
            ex.getStackTraceString)
          log.error(errMsg)
          val errorResponse = new DefaultHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR)
          errorResponse.setContent(copiedBuffer(errMsg, UTF_8))
          errorResponse.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
          errorResponse
      }
    }
  }
  
  def logic(listService: ListService, bankService: BankService) = new HttpService {
    
    def apply(idAndRequest: (ID, HttpRequest)) = {
      val (id, request) = idAndRequest
      val (path, params) = pathAndParams(request)
        
      def handle: Option[Future[HttpResponse]] = {
        val decoder = new QueryStringDecoder(request.getUri)
        lazy val params = Parameters(
          decoder.getParameters.map(entry => (entry._1, entry._2.toList)),
          maxPageSize, defaultPageSize
        )
        val result = decoder.getPath match {
          case "/data/list.json" =>
            Some(futurePool(listService.list(params)))
          case "/data/DP.json" =>
            Some(futurePool(listService.shopIdList(params)))
          case "/data/bank/dealCounts.json" =>
            Some(futurePool(bankService.dealCounts(params)))
          case "/cache/expire" =>
            cache.map(_.flush("_all_"))
            Some(Future("ok"))
          case "/datasource/get" =>
            dataSource match {
              case swds: SwitchableDataSource =>
                Some(Future(swds.currentDataSource))
              case _ =>
                Some(Future("ok"))
            }
          case "/datasource/set" =>
            params.params.get("data_source") match {
              case Some(dataSourceId :: _) =>
                dataSource match {
                  case swds: SwitchableDataSource =>
                    swds.switch(dataSourceId)
                    cache.map(_.flush("_all_"))
                }
                Some(Future("ok"))
              case _ =>
                throw new IllegalArgumentException(
                  String.format("请指定%s(%s)", "数据源id", "datasource"))
            }
          case path => None
        }
        
        result map {
          future => future.map { result =>
            val response = new DefaultHttpResponse(HTTP_1_1, OK)
            response.setContent(copiedBuffer(Mapper.writeValueAsString(result), UTF_8));
            response.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
            response
          }
        }
      }
      handle match {
        case Some(future) =>
          future
        case None =>
          throw HttpException(NOT_FOUND, "Resource Not Found") 
      }
    } 
  }

  private def pathAndParams(request: HttpRequest) = {
    val decoder = new QueryStringDecoder(request.getUri)
    (decoder.getPath, decoder.getParameters.map(e => (e._1, e._2.toList)).toMap)
  }
}
