package com.tapad.druid.client

import org.json4s.JsonAST.{JNull, JArray, JObject, JValue}
import org.json4s.JsonDSL._

sealed trait Filter extends Expression {
  def and(other: Filter) : Filter = And(Seq(this, other))
  def or(other: Filter) : Filter = Or(Seq(this, other))
}

case class And(filters: Seq[Filter]) extends Filter {

  override def and(other: Filter): Filter = copy(other +: filters)

  def toJson: JValue = JObject(
    "type" -> "and",
    "fields" -> JArray(filters.toList.map(_.toJson))
  )
}
case class Or(filters: Seq[Filter]) extends Filter {

  override def or(other: Filter): Filter = copy(other +: filters)

  def toJson: JValue = JObject(
    "type" -> "or",
    "fields" -> JArray(filters.toList.map(_.toJson))
  )
}

case class ExprFilter(typeName: String, dimension: String, value: String) extends Filter {
  def toJson: JValue = JObject(
    "type" -> typeName,
    "dimension" -> dimension,
    "value" -> value
  )
}
case class RegexFilter(dimension: String, pattern: String) extends Filter {
  def toJson: JValue = JObject(
    "type" -> "regex",
    "dimension" -> dimension,
    "pattern" -> pattern
  )
}

object Filter {


  def where(dimension: String, value: String) = ExprFilter("selector", dimension, value)
  def regex(dimension: String, pattern: String) = RegexFilter(dimension, pattern)

  val All = new Filter {
    def toJson: JValue = JNull
  }
}
