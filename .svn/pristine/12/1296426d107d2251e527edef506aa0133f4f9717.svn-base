package com.hui800.listing

import com.twitter.ostrich.admin._
import com.hui800.listing.util._

object Listing extends App {

  val runtime = RuntimeEnvironment(this, args)
  val service = runtime.loadConfig[ListingService]()
  val adminServiceFactory = new AdminServiceFactory(
    httpPort = service.adminPort,
    statsNodes = List(
      new StatsFactory(reporters = List(new TimeSeriesCollectorFactory))
    )
  )
  adminServiceFactory(runtime)
  service.start
  ServiceTracker.register(service)
}
