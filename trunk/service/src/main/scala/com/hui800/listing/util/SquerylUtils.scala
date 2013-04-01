package com.hui800.listing.util

import org.squeryl.dsl._
import org.squeryl.dsl.ast._
import org.squeryl.internals.OutMapper
import org.squeryl.internals.StatementWriter

object SquerylUtils {

  def bitAnd(
    left: NumericalExpression[Int],
    right: NumericalExpression[Int]
  )(implicit m: OutMapper[Int]) = {
    new BitAnd(left, right, m)
  }
  
  def groupConcat(
    expr:      List[StringExpression[String]],
    orderBy:   List[OrderByArg] = Nil,
    separator: String = null
  )(implicit m:OutMapper[String]) = {
    new GroupConcat(expr, orderBy.map(new OrderByExpression(_)), separator, m)
  }
}

class BitAnd(left: NumericalExpression[Int], right: NumericalExpression[Int], m: OutMapper[Int])
extends FunctionNode[Int]("&", Some(m), Seq(left, right)) with NumericalExpression[Int] {
  override def doWrite(sw: StatementWriter) = {
    sw.write("(")
    left.write(sw)
    sw.write(" & ")
    right.write(sw)
    sw.write(")")
  }
}

class GroupConcat(
  e: List[StringExpression[String]],
  o: List[OrderByExpression],
  s: String,
  m: OutMapper[String]
) extends FunctionNode[String]("group_concat", Some(m), e) with StringExpression[String] {
  override def doWrite(sw: StatementWriter) = {
    sw.write(name)
    sw.write("(")
    sw.writeNodesWithSeparator(args, ", ", false)
    if (o.nonEmpty) {
      sw.write(" order by ")
      sw.writeNodesWithSeparator(o, ", ", false)
    }
    if (s != null) {
      sw.write(" separator '")
      sw.write(s)
      sw.write("'")
    }
    sw.write(")")
  }
}
