import com.twitter.logging._
import com.twitter.util.Config._
import com.hui800.listing._
import com.hui800.listing.cache._
import com.hui800.util.core._

val formatter = new Formatter(prefix = "%.3s [<yyyyMMdd-HH:mm:ss.SSS>] <id> %s: ") {
  override def formatPrefix(level: java.util.logging.Level, date: String, name: String): String = {
    super.formatPrefix(level, date, name).replace("<id>", IDManager.get.map(_.toString).getOrElse(""))
  }
}
lazy val consoleHandler = ConsoleHandler(formatter = formatter)
lazy val fileHandler = FileHandler("target/logs/hui800-listing.log", Policy.Daily, true, 7, formatter = formatter)

Logger.configure(
  List(
    LoggerFactory("", Logger.INFO, List(consoleHandler)),
    LoggerFactory("com.hui800.listing", Logger.DEBUG),
    LoggerFactory("squeryl", Logger.DEBUG)
  )
)

new ListingConfig {
  machineId = 1.toShort
  adminPort = 7150
  port = 8150
  maxPageSize = 50
  defaultPageSize = 20
  dataCache = new LRUMapCache("data", 100000)
  responseCache = new LRUMapCache("response", 100000)
//  dataCache = new RedisCache("data", "127.0.0.1", 6379, password = "", index = 0)
//  responseCache = new RedisCache("response", "127.0.0.1", 6379, password = "", index = 1)
}
