package com.hui800.listing.cache

import java.util.Calendar
import java.util.Date
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import scala.collection.JavaConversions._
import com.twitter.logging.Logger

class RedisCache(
  val name: String,
  host: String,
  port: Int,
  timeout: Int = Protocol.DEFAULT_TIMEOUT,
  password: String,
  index: Int = 0
) extends Cache {
  private val jedisConfig = new JedisPoolConfig
  private val pool = new JedisPool(jedisConfig, host, port, timeout, password)
  private val objSerial = new Serializer
  private val logger = Logger(getClass.getName)
  private val _jedis = new ThreadLocal[Jedis]
  
  def put(region: String, key: Any, value: Any) = {
    withJedis { jedis =>
      val k = serialKey(getKeyString(region, key))
      jedis.set(k, objSerial.serialize(value))
      jedis.expire(k, 1800)
    }
  }
  
  def putMulti(region: String, key: Any => Any, values: Any*) = {
    withJedis { jedis =>
      val mp = values.map{v => (serialKey(getKeyString(region, key)), objSerial.serialize(v))}
      val paramArr = mp.flatMap{case(k, v) => List(k, v)} 
      jedis.mset(paramArr: _*)
      mp.map(kv => jedis.expire(kv._1, 1800))
    }
  }
  
  def get[V](region: String, key: Any): Option[V] = {
    withJedis { jedis =>
      Option(objSerial.deserialize(jedis.get(serialKey(getKeyString(region, key)))).asInstanceOf[V])
    }
  }
  
  def getOrElseUpdate[V](region: String, key: Any, default: => V) = {
    withJedis { jedis =>
      Option(objSerial.deserialize(jedis.get(serialKey(getKeyString(region, key)))).asInstanceOf[V]).getOrElse({
          val defval = default
          put(region, key, defval)
          defval
        })
    }
  }
  
  def getMulti[K, V](region: String, keys: Seq[K]): Map[K, V] = {
    withJedis { jedis =>
      val ks = keys.map{ k => (serialKey(getKeyString(region, k)))}
      val bArray =  jedis.mget(ks: _*)
      val result = bArray map { vByte => 
        objSerial.deserialize(vByte).asInstanceOf[V]
      }
      keys.zip(result).filterNot(_._2 == null).toMap
    }
  } 
  
  def remove(region: String, key: Any): Unit = {
    withJedis { jedis =>
      jedis.del(serialKey(getKeyString(region, key)))
    }
  }
  
  // Redis cache uses 30-minutes-expiration strategy
  def flush(region: String) = {}
  
  private def withJedis[T](f: Jedis => T) = {
    var jedis = _jedis.get
    val create = jedis == null
    if (create) {
      jedis = pool.getResource
      _jedis.set(jedis)
    }
    try {
      jedis.select(index)
      f(jedis)
    } finally {
      if (create) {
        _jedis.remove
        pool.returnResourceObject(jedis)
      }
    }
  }
  
  private def delay = {
    val cal = Calendar.getInstance
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 1)
    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.getTimeInMillis - new Date().getTime
  }
  
  private def serialKey(source: String) = {
    if(source != null)  source.getBytes("UTF8") else  null
  }
}
