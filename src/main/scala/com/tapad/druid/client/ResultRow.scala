package com.tapad.druid.client

case class ResultRow(values: Map[String, Any]) {
  def apply[T](col: String) : T = values(col).asInstanceOf[T]
  def get[T](col: String) : Option[T] = values.get(col).asInstanceOf[Option[T]]
  def getOrElse[T](col: String, default: T) : T = values.get(col).asInstanceOf[Option[T]].getOrElse(default)
}
