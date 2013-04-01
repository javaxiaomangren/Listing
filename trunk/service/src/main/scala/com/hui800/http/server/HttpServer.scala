package com.hui800.http.server

import com.hui800.util.core._
import com.twitter.finagle.Filter
import com.twitter.finagle.Service
import com.twitter.finagle.SimpleFilter
import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import com.twitter.finagle.stats.OstrichStatsReceiver
import com.twitter.logging.Logger
import com.twitter.ostrich.stats.Stats
import com.twitter.util.Duration
import com.twitter.util.Local
import com.twitter.util.TimeConversions._
import java.net.InetSocketAddress
import java.net.URI
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.util.CharsetUtil._
import scala.collection.JavaConversions._

object HttpServer {
  
  val logger = Logger(getClass.getName)
  
  private val DEFAULT_CONTROLLER: Controller = {
    case request: HttpRequest => throw HttpException(NOT_FOUND, "Resource Not Found")
  }
  
  private val DEFAULT_ERROR_HANDLER: ErrorHandler = {
    case ex: HttpException => 
      val errorResponse = new DefaultHttpResponse(HTTP_1_1, ex.status)
      errorResponse.setContent(copiedBuffer(ex.errMsg, UTF_8))
      errorResponse.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8")
      HttpResponse(errorResponse)
    case ex: Throwable =>
      val errMsg = String.format(
        "%s: %s\n%s",
        ex.getClass.getName,
        ex.getMessage,
        ex.getStackTraceString
      )
      logger.error(errMsg)
      val errorResponse = new DefaultHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR)
      errorResponse.setContent(copiedBuffer(errMsg, UTF_8))
      errorResponse.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8")
      HttpResponse(errorResponse)
  }
}

import HttpServer._

class HttpServer(
  port: Int,
  controller: Controller = DEFAULT_CONTROLLER,
  errorHandler: ErrorHandler = DEFAULT_ERROR_HANDLER,
  shutdownTimeout: Duration = 5.seconds
) extends com.twitter.ostrich.admin.Service {

  private val logger = Logger(getClass.getName)
  private var server: Server = null
  
  val builder = ServerBuilder()
  .codec(Http())
  .bindTo(new InetSocketAddress(port))
  .name("httpserver")
  .reportTo(new OstrichStatsReceiver)

  def start = {
    server = builder.build(accessLog andThen errHandler andThen logic)
  }
  
  def shutdown = {
    server.close(shutdownTimeout)
  }
  
  lazy val accessLog = new Filter[NettyHttpRequest, NettyHttpResponse, HttpRequest, HttpResponse] {
    def apply(request: NettyHttpRequest, svc: Service[HttpRequest, HttpResponse]) = {
      IDManager.generate
      val req = new HttpRequest(request)
      logger.info("%s %s %s", req.method, req.uri, req.protocolVersion)
      svc(req) map { resp =>
        val status = resp.status
        val delta = System.currentTimeMillis - IDManager.get.get.timestamp
        logger.info("%s %s %s %s %s", req.method, req.uri, req.protocolVersion, status.getCode, delta)
        Stats.addMetric("if:" + new URI(req.uri).getPath, delta.toInt)
        resp.response
      }
    }
  }
  
  lazy val errHandler = new SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, svc: Service[HttpRequest, HttpResponse]) = {  
      svc(request).handle(errorHandler orElse DEFAULT_ERROR_HANDLER)
    }
  }
  
  lazy val logic = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = {
      (controller orElse DEFAULT_CONTROLLER)(req)
    }
  }
}
