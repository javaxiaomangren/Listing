package com.hui800

import com.twitter.finagle._
import org.jboss.netty.handler.codec.http._

package object listing {

  type HttpService = Service[(ID, HttpRequest), HttpResponse]
}
