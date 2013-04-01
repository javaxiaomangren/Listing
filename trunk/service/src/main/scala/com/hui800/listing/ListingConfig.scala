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
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.util.CharsetUtil._
import scala.collection.JavaConversions._

class ListingConfig extends Config[ListingConfig] {
  
  var machineId = required[Short]
  var adminPort = required[Int]
  var port = required[Int]
  var maxPageSize = required[Int]
  var defaultPageSize = required[Int]
  var dataCache = required[Cache](NoCache)
  var responseCache = required[Cache](NoCache)
  
  def apply = this
}
