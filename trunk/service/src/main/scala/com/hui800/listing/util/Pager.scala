package com.hui800.listing.util

object Pager {
  def numOfPages(count: Long, pageSize: Int): Int = {
    (count / pageSize + scala.math.signum(count % pageSize)).toInt
  }
}

case class Pager(skip: Int, size: Int)
