package com.hui800.listing.util

object CollectionUtils {

  def mapToList[K, V <: AnyRef](kvMap: Map[K, V], keys: K*): List[V] = {
    keys collect {
      k =>
      kvMap.get(k) match {
        case Some(v) => v
      }
    } toList
  }
}
