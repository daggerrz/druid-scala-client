package com.tapad.druid.client

import org.json4s.JsonAST.{JObject, JValue}
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

trait PostAggregationFieldSpec extends Expression {
  private def arith(rhs: PostAggregationFieldSpec, fn: String) : PostAggregation = ArithmeticPostAggregation("n/a", fn, Seq(this, rhs))

  def *(rhs: PostAggregationFieldSpec) = arith(rhs, "*")
  def /(rhs: PostAggregationFieldSpec) = arith(rhs, "/")
  def +(rhs: PostAggregationFieldSpec) = arith(rhs, "+")
  def -(rhs: PostAggregationFieldSpec) = arith(rhs, "-")
}
trait PostAggregation extends PostAggregationFieldSpec {
  def as(outputName: String) : PostAggregation
}

object PostAggregation {
  def constant(value: Double) = ConstantPostAggregation("constant", value)

  case class FieldAccess(fieldName: String) extends PostAggregationFieldSpec {
    def toJson: JValue = JObject(
      "type" -> "fieldAccess",
      "fieldName" -> fieldName
    )
  }

}

case class ConstantPostAggregation(outputName: String, value: Double) extends PostAggregation  {
  def toJson: JValue = JObject(
    "type" -> "constant",
    "name" -> outputName,
    "value" -> value
  )

  def as(outputName: String): PostAggregation = copy(outputName = outputName)
}

case class ArithmeticPostAggregation(outputName: String, fn: String, fields: Seq[PostAggregationFieldSpec]) extends PostAggregation {
  def toJson: JValue = JObject(
    "type" -> "arithmetic",
    "name" -> outputName,
    "fn" -> fn,
    "fields" -> fields.map(_.toJson)
  )

  def as(outputName: String) = copy(outputName = outputName)
}