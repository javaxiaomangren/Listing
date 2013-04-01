package com.hui800.listing.cache

object NoCache extends Cache {

  def name: String = null
  
  def put(region: String, key: Any, value: Any) = {}
  
  def putMulti(region: String, key: Any => Any, values: Any*) = {}
  
  def get[V](region: String, key: Any): Option[V] = None
  
  def getOrElseUpdate[V](region: String, key: Any, default: => V) = default
  
  def getMulti[K, V](region: String, keys: Seq[K]): Map[K, V] = Map.empty
  
  def remove(region: String, key: Any): Unit = {}
  
  def flush(region: String) = {}
}
