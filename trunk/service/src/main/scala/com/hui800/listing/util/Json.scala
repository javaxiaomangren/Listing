package com.hui800.listing.util

import com.fasterxml.jackson.databind.SerializationFeature._
import com.fasterxml.jackson.annotation.JsonInclude._

object Json extends com.codahale.jerkson.Json {

  mapper.configure(WRITE_NULL_MAP_VALUES, false)
  mapper.setSerializationInclusion(Include.NON_NULL)
}
