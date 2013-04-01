package com.hui800.listing.util

import com.hui800.listing.svc.model._
import org.codehaus.jackson.io.SegmentedStringWriter
import org.codehaus.jackson.map.ObjectMapper

object Mapper extends ObjectMapper {
  
  _serializationConfig.addMixInAnnotations(classOf[Item],  classOf[Item])
  
  def writeValueAsString(value: Any, view: Class[_]) = {
    val sw = new SegmentedStringWriter(_jsonFactory._getBufferRecycler)
    writeValue(_jsonFactory.createJsonGenerator(sw),
               value, _serializationConfig.withView(view))
    sw.getAndClear
  }
}
