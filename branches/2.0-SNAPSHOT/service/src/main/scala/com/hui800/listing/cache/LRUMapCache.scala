package com.hui800.listing.cache

import java.util.concurrent.locks.ReentrantReadWriteLock
import org.apache.commons.collections.map.LRUMap
import scala.collection.JavaConversions._
import com.twitter.logging.Logger

class LRUMapCache(size: Int = 100000) extends Cache {

  private val lock = new ReentrantReadWriteLock
  private val rlock = lock.readLock
  private val wlock = lock.writeLock
  private[this] val vals = new LRUMap(size)

  private val logger = Logger(getClass.getName)
  
  def put(region: String, key: Any, value: Any) = {
    wlock.lock
    try {
      vals.put(getKeyString(region, key), value)
    }
    finally wlock.unlock
  }
  
  def putMulti(region: String, key: Any => Any, values: Any*) = {
    wlock.lock
    try {
      values foreach(v => vals.put(getKeyString(region, key(v)), v))
    }
    finally wlock.unlock
  }
  
  def get[V](region: String, key: Any): Option[V] = {
    get(getKeyString(region, key))
  }
  
  private def get[V](key: String): Option[V] = {
    rlock.lock
    try {
      Option(vals.get(key).asInstanceOf[V])
    }
    finally rlock.unlock
  }
  
  def getOrElseUpdate[V](region: String, key: Any, default: => V) = {
    val keyString = getKeyString(region, key)
    get(keyString) match {
      case Some(cached) => cached
      case None =>
        wlock.lock
        try {
          get(keyString) match {
            case Some(cached) => cached
            case None =>
              val value = default
              vals.put(keyString, value)
              value
          }
        }
        finally wlock.unlock
    }
  }
  
  def getMulti[K, V](region: String, keys: Seq[K]): Map[K, V] = {
    rlock.lock
    try {
      val map = Map.empty[K, V]
      (keys collect { k =>
          vals.get(getKeyString(region, k)) match {
            case v: V => (k, v)
          }
        }).toMap
    }
    finally rlock.unlock
  }
  
  def remove(region: String, key: Any): Unit = {
    wlock.lock
    try {
      vals.remove(getKeyString(region, key))
    }
    finally wlock.unlock
  }
  
  def flush(region: String) = region match {
    case "_all_" =>
      wlock.lock
      try {
        vals.clear
      }
      finally wlock.unlock
    case other: String if !other.trim.isEmpty =>
      throw new IllegalArgumentException("LRUMapCache does not support regional flush.")
    case _ =>
      throw new IllegalArgumentException("Please specify the region to be flushed")
  }
}
