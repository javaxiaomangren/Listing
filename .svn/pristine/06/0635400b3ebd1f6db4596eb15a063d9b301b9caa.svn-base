package com.hui800.listing.cache

class Invocation[T, R] private (val sig: (T => R, T))

object Invocation {
  
  import java.lang.ref.WeakReference
  import java.util.WeakHashMap
  import java.util.concurrent.locks.ReentrantReadWriteLock
  
  private val rwl = new ReentrantReadWriteLock()
  private val rlock = rwl.readLock
  private val wlock = rwl.writeLock
  private val map = new WeakHashMap[(_ => _, _), WeakReference[Invocation[_, _]]]
  
  private def valueFromKey[T, R](k: (T => R, T)) = new Invocation((k))
  private def keyFromValue[T, R](v: Invocation[T, R]) = Some(v.sig)
  
  def apply[T, R](name: (T => R, T)): Invocation[T, R] = {
    def cached(): Invocation[T, R] = {
      rlock.lock
      try {
        val reference = map.asInstanceOf[WeakHashMap[(T => R, T), WeakReference[Invocation[T, R]]]] get name
        if (reference == null) null
        else reference.get  // will be null if we were gc-ed
      }
      finally rlock.unlock
    }
    def updateCache(): Invocation[T, R] = {
      wlock.lock
      try {
        val res = cached()
        if (res != null) res
        else {        
          val sym = valueFromKey(name)
          map.put(name, new WeakReference(sym))
          sym
        }
      }
      finally wlock.unlock
    }
    
    val res = cached()
    if (res == null) updateCache()
    else res
  }
  def unapply[T, R](other: Invocation[T, R]) = keyFromValue(other)
}

