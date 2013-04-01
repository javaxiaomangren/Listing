package com.hui800.listing.data

import com.twitter.querulous.evaluator._
import com.twitter.querulous.query._
import com.twitter.querulous.database._
import com.twitter.util.TimeConversions._

class Source {

  private val queryFactory = new SqlQueryFactory
  private val databaseFactory = new ApachePoolingDatabaseFactory(
    minOpenConnections = 1,
    maxOpenConnections = 10,
    checkConnectionHealthWhenIdleFor = 30.seconds,
    maxWaitForConnectionReservation = 60.seconds,
    checkConnectionHealthOnReservation = true,
    evictConnectionIfIdleFor = 60.seconds,
    defaultUrlOptions = Map.empty
  )
  private val queryEvaluatorFactory = new StandardQueryEvaluatorFactory(databaseFactory, queryFactory)
  val queryEvaluator = queryEvaluatorFactory(List("primaryhost", "fallbackhost1", "fallbackhost2"), "username", "password")
}
