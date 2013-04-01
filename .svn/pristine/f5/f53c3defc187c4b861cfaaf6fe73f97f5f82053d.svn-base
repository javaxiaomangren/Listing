package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing._
import com.hui800.listing.cache._
import com.hui800.listing.util._
import com.twitter.util.Future

abstract class AbstractController(val prefix: String) extends Controller {
  
  def isDefinedAt(request: HttpRequest) = {
    request.path.startsWith(prefix)
  }
  
  def apply(request: HttpRequest) = {
    respond(request) map { 
      case CachedValue(content, stale) =>
        HttpResponse(Json.generate(content), stale = stale)
      case FreshValue(content) =>
        HttpResponse(Json.generate(content))
      case content =>
        HttpResponse(Json.generate(content))
    }
  }
  
  def respond(request: HttpRequest): Future[Any]
}
