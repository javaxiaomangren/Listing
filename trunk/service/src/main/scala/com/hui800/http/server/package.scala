package com.hui800.http

import com.twitter.util.Future

package object server {

  type NettyHttpRequest = org.jboss.netty.handler.codec.http.HttpRequest
  type NettyHttpResponse = org.jboss.netty.handler.codec.http.HttpResponse
  type ErrorHandler = PartialFunction[Throwable, HttpResponse]
  type Controller = PartialFunction[HttpRequest, Future[HttpResponse]]
}
