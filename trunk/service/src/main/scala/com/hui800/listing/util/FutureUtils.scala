package com.hui800.listing.util

import com.twitter.util.FuturePool
import java.util.concurrent.Executors

object FutureUtils {

  val futurePool = FuturePool(Executors.newCachedThreadPool)
}
