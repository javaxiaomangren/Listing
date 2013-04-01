package com.hui800.listing.util

import org.squeryl.dsl.NumericalExpression
import org.squeryl.dsl.ast.FunctionNode
import org.squeryl.internals.OutMapper
import org.squeryl.internals.StatementWriter

object SquerylUtils {

  def bitAnd(left: NumericalExpression[Int], right: NumericalExpression[Int])(implicit m: OutMapper[Int]) = new BitAnd(left, right, m)
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
