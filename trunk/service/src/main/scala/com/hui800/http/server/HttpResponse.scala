package com.hui800.http.server

import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.util.CharsetUtil._

object HttpResponse {
  
  def apply(
    content: Any,
    status: HttpResponseStatus = OK,
    httpVersion: HttpVersion = HTTP_1_1,
    stale: Boolean = false
  ) = {
    val response = new DefaultHttpResponse(httpVersion, status)
    response.setContent(copiedBuffer(content.toString, UTF_8))
    if (stale) {
      response.setHeader("Warning", "110 hui800-listing Data is stale")
    }
    new HttpResponse(response)
  }
}

case class HttpResponse(
  response: NettyHttpResponse
) {
  def status = response.getStatus
}
