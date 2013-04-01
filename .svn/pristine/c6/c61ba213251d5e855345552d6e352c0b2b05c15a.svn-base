package com.hui800.listing

import com.twitter.ostrich.admin._
import com.twitter.util.Config
import com.twitter.util.Config._
import org.squeryl.adapters._
import com.hui800.listing.cache._
import com.hui800.listing.db._
import com.hui800.listing.db.impl._
import com.hui800.listing.svc._
import com.hui800.listing.svc.impl._
import com.twitter.util.FuturePool
import java.util.concurrent.Executors
import javax.sql.DataSource
import org.apache.commons.configuration.PropertiesConfiguration
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.util.CharsetUtil._
import scala.collection.JavaConversions._

case class ListingConfig(
  machineId: Required[Short],
  adminPort: Required[Int],
  port: Required[Int],
  maxPageSize: Required[Int],
  defaultPageSize: Required[Int],
  cache: Required[Option[Cache]]
) extends Config[ListingService] {
  
  def apply = new ListingService(
    machineId = machineId.value,
    adminPort = adminPort.value,
    port = port.value,
    maxPageSize = maxPageSize.value,
    defaultPageSize = defaultPageSize.value,
    dataSource = new SwitchableDataSource(new PropertiesConfiguration("conf/db.properties")),
    cache = cache.value,
    futurePool = FuturePool(Executors.newCachedThreadPool)
  )
}
