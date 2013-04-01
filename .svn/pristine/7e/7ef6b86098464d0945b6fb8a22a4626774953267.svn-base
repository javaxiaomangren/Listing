package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.cache._
import com.twitter.logging.Logger
import com.twitter.util.Future

class CacheController(
  dataCache: Cache,
  responseCache: Cache
) extends AbstractController("/cache/") {

  val logger = Logger(getClass.getName)
  
  def respond(request: HttpRequest): Future[Any] = {
    request.path.substring(prefix.length) match {
      case "expire" =>
        flush
        Future("ok")
      case _ => throw HttpException.RESOURCE_NOT_FOUND
    }
  }
  
  def flush = {
    dataCache.flush("_all_")
    responseCache.timestamp = System.currentTimeMillis
  }
}
