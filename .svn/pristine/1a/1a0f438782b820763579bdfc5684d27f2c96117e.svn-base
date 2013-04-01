package com.hui800.listing.cache

import java.text.SimpleDateFormat
import java.util.Date

trait Cache {

  private val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS")
  
  var timestamp = System.currentTimeMillis
  
  def name: String
  
  def put(prefix: String, key: Any, entity: Any): Unit
  
  def putMulti(prefix: String, key: Any => Any, entities: Any*): Unit
  
  def get[V](prefix: String, key: Any): Option[V]
  
  def getOrElseUpdate[V](prefix: String, key: Any, default: => V): V
  
  def getMulti[K, V](prefix: String, key: Seq[K]): Map[K, V]
  
  def remove(prefix: String, key: Any): Unit
  
  def flush(prefix: String): Unit
  
  def getKeyString(prefix: String, key: Any) = {
    String.format("%s(%s)", prefix, key match {
        case ck: CacheKey =>
          ck.toCacheKey
        case t2: Product =>
          (t2.productIterator.toList.map {
              case ck: CacheKey => ck.toCacheKey
              case None => "null"
              case Some(date: Date) => dateFormat.format(date)
              case date: Date => dateFormat.format(date)
              case Some(any) => any.toString
              case any => any.toString
            }).mkString(",")
        case _ =>
          key.asInstanceOf[AnyRef].toString
      }
    )
  }
}

trait CacheComponent {
  val  cache: Cache
}
