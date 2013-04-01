package com.hui800.http.server

import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class Parameters(val params: Map[String, List[String]]) {

  def optional(name: String): Option[String] = {
    params.get(name).flatMap(_.headOption)
  }
  
  def optional[T <: Any : Manifest](name: String, conv: String => T): Option[T] = {
    optional(name).map(tryConv(name, conv))
  }

  def optionalList(name: String): Option[List[String]] = {
    params.get(name)
  }
  
  def optionalList(name: String, delimiter: String): Option[List[String]] = {
    delimiter match {
      case delim: String =>
        optional(name).map(_.split(delim).toList)
      case _ =>
        optionalList(name)
    }
  }
  
  def optionalList[T <: Any : Manifest](
    name:      String,
    conv:      String => T,
    delimiter: String = null
  ): Option[List[T]] = {
    optionalList(name, delimiter).map(_.map(tryConv(name, conv)))
  }
  
  def required(name: String): String = {
    optional(name).getOrElse(
      throw new HttpException(BAD_REQUEST, "Param '%s' is required.".format(name))
    )
  }
  
  def required[T <: Any : Manifest](name: String, conv: String => T): T = {
    optional(name, conv).getOrElse(
      throw new HttpException(BAD_REQUEST, "Param '%s' is required.".format(name))
    )
  }
  
  def requiredList(name: String): List[String] = {
    optionalList(name).getOrElse(
      throw new HttpException(BAD_REQUEST, "Param '%s' is required.".format(name))
    )
  }
  
  def requiredList(name: String, delimiter: String): List[String] = {
    optionalList(name, delimiter).getOrElse(
      throw new HttpException(BAD_REQUEST, "Param '%s' is required.".format(name))
    )
  }
  
  def requiredList[T <: Any : Manifest](
    name:      String,
    conv:      String => T,
    delimiter: String = null
  ): List[T] = {
    optionalList(name, conv, delimiter).getOrElse(
      throw new HttpException(BAD_REQUEST, "Param '%s' is required.".format(name))
    )
  }
  
  private def tryConv[T <: Any : Manifest](name: String, conv: String => T)(value: String) = {
    val m = manifest[T]
    try {
      conv(value)
    } catch {
      case ex => throw new HttpException(
          BAD_REQUEST,
          "Param value cannot be convert to %s. (name = '%s', value = '%s')"
          .format(m, name, value)
        )
    }    
  }
}
