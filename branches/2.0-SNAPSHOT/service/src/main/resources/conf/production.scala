import com.twitter.logging._
import com.twitter.util.Config._
import com.hui800.listing._
import com.hui800.listing.cache._

lazy val consoleHandler = ConsoleHandler()
lazy val fileHandler = FileHandler("log/hui800-listing.log", Policy.Daily, true, 7)

Logger.configure(
  List(
    LoggerFactory("", Logger.INFO, List(consoleHandler)),
    LoggerFactory("com.hui800.listing", Logger.DEBUG)
  )
)

ListingConfig(
  machineId = 1.toShort,
  adminPort = 8201,
  port = 8200,
  maxPageSize = 50,
  defaultPageSize = 20,
  cache = new LRUMapCache(100000)
)
