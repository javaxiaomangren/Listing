package com.hui800.listing

import com.twitter.logging.Logger
import com.twitter.ostrich.admin._
import com.hui800.util.core._
import com.hui800.http.server._
import com.hui800.listing.util._
import com.hui800.listing.db._
import com.hui800.listing.db.cache._
import com.hui800.listing.db.impl._
import com.hui800.listing.controller._
import com.hui800.listing.cache._
import com.hui800.listing.svc.cache._
import com.hui800.listing.svc.impl._
import org.apache.commons.configuration.PropertiesConfiguration
import org.squeryl.adapters.MySQLAdapter

object Main extends App {

  val logger = Logger(getClass.getName)
  val runtime = RuntimeEnvironment(this, args)
  val config = runtime.loadConfig[ListingConfig]()
  
  val adminServiceFactory = new AdminServiceFactory(
    httpPort = config.adminPort,
    statsNodes = List(
      new StatsFactory(reporters = List(new TimeSeriesCollectorFactory))
    )
  )
  adminServiceFactory(runtime)
  
  IDManager.machineId = config.machineId
  
  val dataSource = new SwitchableDataSource(new PropertiesConfiguration("conf/db.properties"))

  val db = new DB(dataSource, new MySQLAdapter)
  
  trait DataCache extends CacheComponent {
    val cache = config.dataCache.value
  }
  
  trait ResponseCache extends CacheComponent {
    val cache = config.responseCache.value
  }
  
  val dealQueries = new DealQueriesImpl(db) with DealQueriesCache with DataCache
  val shopQueries = new ShopQueriesImpl(db) with ShopQueriesCache with DataCache
  val regionQueries = new RegionQueriesImpl(db) with RegionQueriesCache with DataCache
  val creditCardQueries = new CreditCardQueriesImpl(db) with CreditCardQueriesCache with DataCache
  val mallQueries = new MallQueriesImpl(db) with MallQueriesCache with DataCache
  
  val creditCardService = new CreditCardServiceImpl(creditCardQueries)
  val dealCountService = new DealCountServiceImpl(dealQueries)
  val shopCountService = new ShopCountServiceImpl(shopQueries, dealQueries)
  val regionService = new RegionServiceImpl(regionQueries)
  val bankService = new BankServiceImpl(dealCountService)
  val mallService = new MallServiceImpl(mallQueries)
  val listService = new ListServiceImpl(
    dealQueries = dealQueries,
    shopQueries = shopQueries,
    regionService = regionService,
    shopCountService = shopCountService,
    dealCountService = dealCountService
  )
  
  val dataController = new DataController(listService, bankService, config.defaultPageSize, config.maxPageSize) with ResponseCache
  val creditCardController = new CreditCardController(creditCardService, config.defaultPageSize, config.maxPageSize)
  val mallController = new MallController(mallService, config.defaultPageSize, config.maxPageSize)
  val cacheController = new CacheController(config.dataCache, config.responseCache)
  val dataSourceController = new DataSourceController(dataSource, cacheController)

  val server = new HttpServer(
    config.port,
    dataController orElse
    creditCardController orElse
    mallController orElse
    cacheController orElse
    dataSourceController
  )
  server.start
  logger.info("HTTP interface started on port %d", config.port.value)
  ServiceTracker.register(server)
}
