package com.tapad.druid.client

import org.json4s.JsonAST.JValue

trait Expression {
  def toJson : JValue
}
