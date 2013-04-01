organization := "com.hui800.listing"

name := "listing-service"

version := "2.0-SNAPSHOT"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "twitter" at "http://maven.twttr.com",
  "codahale" at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
  "com.twitter" % "ostrich" % "8.2.1",
  "com.twitter" % "finagle-ostrich4" % "5.3.8",
  "com.twitter" % "finagle-http" % "5.3.8",
  "org.squeryl" %% "squeryl" % "0.9.5-2",
  "redis.clients" % "jedis" % "2.1.0",
  "c3p0" % "c3p0" % "0.9.1.2",
  "commons-configuration" % "commons-configuration" % "1.8",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.2",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.2",
  "org.slf4j" % "slf4j-jdk14" % "1.7.2",
  "io.backchat.jerkson" %% "jerkson" % "0.7.0"
)
