package com.tapad.druid.client

import org.json4s.JsonAST._
import org.json4s.JsonDSL._

sealed trait Aggregation extends Expression {
  def as(outputName: String) : Aggregation
}
case class BaseAggregation(typeName: String, outputName: String, fieldName: String) extends Aggregation {
  def toJson = JObject(
    "type" -> typeName,
    "name" -> outputName,
    "fieldName" -> fieldName
  )

  def as(outputName: String) = copy(outputName = outputName)
}


