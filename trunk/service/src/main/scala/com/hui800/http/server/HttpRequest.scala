package com.hui800.http.server

import com.hui800.util.core._
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import scala.collection.JavaConversions._

case class HttpRequest(request: NettyHttpRequest) {
  lazy val decoder = new QueryStringDecoder(request.getUri)
  lazy val params = new Parameters(decoder.getParameters.map(entry => (entry._1, entry._2.toList)).toMap)
  lazy val path = decoder.getPath
  lazy val method = request.getMethod
  lazy val uri = request.getUri
  lazy val protocolVersion = request.getProtocolVersion
}
