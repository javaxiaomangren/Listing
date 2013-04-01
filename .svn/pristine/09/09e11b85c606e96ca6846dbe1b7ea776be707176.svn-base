package com.hui800.listing.controller

import com.hui800.http.server._
import com.hui800.listing.db._
import com.hui800.listing.cache._
import com.twitter.logging.Logger
import com.twitter.util.Future
import javax.sql.DataSource

class DataSourceController(
  dataSource: DataSource,
  cacheController: CacheController
) extends AbstractController("/datasource/") {

  val logger = Logger(getClass.getName)
  
  def respond(request: HttpRequest): Future[Any] = {
    request.path.substring(prefix.length) match {
      case "get" => 
        dataSource match {
          case swds: SwitchableDataSource =>
            Future(swds.currentDataSource)
          case _ =>
            Future("ok")
        }
      case "set" =>
        val dataSourceId = request.params.required("data_source")
        dataSource match {
          case swds: SwitchableDataSource =>
            swds.switch(dataSourceId)
            cacheController.flush
        }
        Future("ok")
      case _ => throw HttpException.RESOURCE_NOT_FOUND
    }
  }
}
