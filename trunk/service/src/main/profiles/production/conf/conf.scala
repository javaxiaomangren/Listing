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
lazy val fileHandler = FileHandler("logs/hui800-listing.log", Policy.Daily, true, 30, formatter = formatter)

Logger.configure(
  List(
    LoggerFactory("", Logger.INFO, List(fileHandler))
  )
)

new ListingConfig {
  machineId = 1.toShort
  adminPort = 7150
  port = 8150
  maxPageSize = 50
  defaultPageSize = 20
  dataCache = new RedisCache("data", "192.168.1.16", 6379, password = "Rehui15Ids", index = 0)
  responseCache = new RedisCache("response", "192.168.1.16", 6379, password = "Rehui15Ids", index = 1)
}
