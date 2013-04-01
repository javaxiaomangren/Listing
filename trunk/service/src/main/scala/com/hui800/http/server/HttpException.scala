package com.hui800.http.server

import org.jboss.netty.handler.codec.http.HttpResponseStatus

object HttpException {

  val RESOURCE_NOT_FOUND = new HttpException(HttpResponseStatus.NOT_FOUND, "Resource not found.")
}

case class HttpException(
  status: HttpResponseStatus,
  errMsg: String
) extends Exception(errMsg)
