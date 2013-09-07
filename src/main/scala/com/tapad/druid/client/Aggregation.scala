package com.tapad.druid.client

import org.json4s.JsonAST._
import org.json4s.JsonDSL._

case class Aggregation(typeName: String, fieldName: String, outputName: String) extends Expression {
  def toJson = JObject(
    "type" -> typeName,
    "name" -> outputName,
    "fieldName" -> fieldName
  )

  def as(outputName: String) = copy(outputName = outputName)
}


