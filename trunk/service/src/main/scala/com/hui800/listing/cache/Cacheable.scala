package com.hui800.listing.cache

object Cacheable {
  implicit def wrap[T](value: T): Cacheable[T] = FreshValue(value)
}

trait Cacheable[+T] {

  def value: T
  
  def map[R](f: T => R): Cacheable[R]
}

case class CachedValue[T](value: T, stale: Boolean = false) extends Cacheable[T] {
  def map[R](f: T => R) = CachedValue(f(value), stale)
}

case class FreshValue[T](value: T) extends Cacheable[T] {
  def map[R](f: T => R) = FreshValue(f(value))
}
