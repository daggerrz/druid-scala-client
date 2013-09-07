package com.tapad.druid.client

import org.json4s.JsonAST._
import org.json4s.JsonDSL._

case class ColumnOrder(columnName: String, direction: String) extends Expression {
  def toJson: JValue = JObject(
    "dimension" -> columnName,
    "direction" -> direction
  )
}
case class OrderBy(cols: Seq[ColumnOrder], limit : Option[Int] = None) extends Expression {
  def toJson: JValue = JObject(
    "type" -> "default",
    "columns" -> cols.map(_.toJson),
    "limit" -> limit.map(i => JInt(BigInt(i))).getOrElse(JNull)
  )
}
