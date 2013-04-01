package com.hui800.listing.cache

import com.twitter.logging.Logger
import com.twitter.ostrich.stats.Stats

class Memoize1[-T, +R](prefix: String, cache: Cache, f: T => R) extends (T => R) {
  
  val logger = Logger(getClass.getName)
  
  def apply(x: T): R = {
    cache.get[(Long, R)](prefix, x) match {
      case Some((timestamp, value)) =>
        if (timestamp > cache.timestamp) {
          Stats.incr("cache/hit")
          logger.debug("cache hit: %s, %s", prefix, x)
          value
        } else {
          Stats.incr("cache/expire")
          logger.debug("cached data expired: %s, %s", prefix, x)
          val inv = Invocation((f, x))
          inv.synchronized {
            cache.get[(Long, R)](prefix, x) match {
              case Some((timestamp, value))
                if timestamp > cache.timestamp =>
                logger.debug("already refreshed by others")
                value
              case _ =>
                logger.debug("refreshing data: %s, %s", prefix, x)                      
                val value = f(x)
                cache.put(prefix, x, (System.currentTimeMillis, value))
                value
            }
          }
        }
      case _ =>
        Stats.incr("cache/miss")
        logger.debug("cache missed: %s, %s", prefix, x)
        val inv = Invocation((f, x))
        inv.synchronized {
          cache.get[(Long, R)](prefix, x) match {
            case Some((timestamp, value))
              if timestamp > cache.timestamp =>
              logger.debug("already refreshed by others")
              value
            case _ =>
              logger.debug("refreshing data: %s, %s", prefix, x)                      
              val value = f(x)
              cache.put(prefix, x, (System.currentTimeMillis, value))
              value
          }
        }
    }
  }
}

class ListMemo1[K, V](prefix: String, cache: Cache, f: (K*) => List[V], g: V => K) extends ((K*) => List[V]) {
  
  def apply(keys: K*) = {
    if (keys.isEmpty) Nil
    else {
      var cachedValues = cache.getMulti[K, V](prefix, keys)
      val missingKeys = keys collect { k =>
        cachedValues.get(k) match {
          case None => k
        }
      }
      if (!missingKeys.isEmpty) {
        val missingValues = f(missingKeys: _*).map(v => (g(v), v)).toMap
        missingKeys foreach { k => cache.put(prefix, k, missingValues.getOrElse(k, null.asInstanceOf[V])) }
        cachedValues ++= missingValues
      }
      (keys collect { k =>
          cachedValues.get(k) match {
            case Some(v: V) => v
          }
        }).toList
    }
  }
}

class ListMemo2[T, K, V](prefix: String, cache: Cache, f: (T, K*) => List[V], g: V => K) extends ((T, K*) => List[V]) {
  
  def apply(t: T, keys: K*) = {
    if (keys.isEmpty) Nil
    else {
      val adjustedPrefix = String.format(
        "%s(%s)", prefix, t match {
          case ck: CacheKey => ck.toCacheKey
          case _ => t.toString
        })
      var cachedValues = cache.getMulti[K, V](adjustedPrefix, keys)
      val missingKeys = keys collect { k =>
        cachedValues.get(k) match {
          case None => k
        }
      }
      if (!missingKeys.isEmpty) {
        val missingValues = f(t, missingKeys: _*).map(v => (g(v), v)).toMap
        missingKeys foreach { k => cache.put(adjustedPrefix, k, missingValues.getOrElse(k, null.asInstanceOf[V])) }
        cachedValues ++= missingValues
      }
      (keys collect { k =>
          cachedValues.get(k) match {
            case Some(v: V) => v
          }
        }).toList
    }
  }
}

trait Memo {
  
  this: CacheComponent =>
  
  def memoize[T,R]
  (prefix: String,f: T => R)
  : (T => R) = {
    new Memoize1(prefix,cache,f)
  }
  
  def memoize[T1,T2,R]
  (prefix: String,f: (T1,T2) => R)
  : ((T1,T2) => R) = {
    Function.untupled(memoize(prefix, f.tupled))
  }

  def memoize[T1,T2,T3,R]
  (prefix: String,f: (T1,T2,T3) => R)
  : ((T1,T2,T3) => R) = {
    Function.untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,R]
  (prefix: String,f: (T1,T2,T3,T4) => R)
  : ((T1,T2,T3,T4) => R) = {
    Function.untupled(memoize(prefix, f.tupled))
  }
    
  def memoize[T1,T2,T3,T4,T5,R]
  (prefix: String,f: (T1,T2,T3,T4,T5) => R)
  : ((T1,T2,T3,T4,T5) => R) = {
    Function.untupled(memoize(prefix, f.tupled))
  }

  def memoize[T1,T2,T3,T4,T5,T6,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6) => R)
  : ((T1,T2,T3,T4,T5,T6) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7) => R)
  : ((T1,T2,T3,T4,T5,T6,T7) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8) => R) = {
    untupled(memoize(prefix, f.tupled))
  }

  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoize[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R]
  (prefix: String,f: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14) => R)
  : ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14) => R) = {
    untupled(memoize(prefix, f.tupled))
  }
  
  def memoizeList[K, V](prefix: String, f: (K*) => List[V], g: V => K) = new ListMemo1(prefix, cache, f, g)
  
  def memoizeList[T, K, V](prefix: String, f: (T, K*) => List[V], g: V => K) = new ListMemo2(prefix, cache, f, g)
  
  def untupled[T1,T2,T3,T4,T5,T6,R]
  (f: ((T1,T2,T3,T4,T5,T6)) => R)
  :(T1,T2,T3,T4,T5,T6) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6) =>
    f(a1,a2,a3,a4,a5,a6)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7)) => R)
  :(T1,T2,T3,T4,T5,T6,T7) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7) =>
    f(a1,a2,a3,a4,a5,a6,a7)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9,a10:T10) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9,a10:T10,a11:T11) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9,a10:T10,a11:T11,a12:T12) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12)
  }
  
  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9,a10:T10,a11:T11,a12:T12,a13:T13) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13)
  }

  def untupled[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R]
  (f: ((T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14)) => R)
  :(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14) => R  = {
    (a1:T1,a2:T2,a3:T3,a4:T4,a5:T5,a6:T6,a7:T7,a8:T8,a9:T9,a10:T10,a11:T11,a12:T12,a13:T13,a14:T14) =>
    f(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13,a14)
  }
}
