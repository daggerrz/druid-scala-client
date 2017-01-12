package com.tapad.druid.client

import org.json4s.JsonAST._
import org.json4s.JsonDSL._

import scala.concurrent.duration._

trait Granularity {
  def toJson: JValue
}

case class SimpleGranularity(name: String) extends Granularity {
  def toJson = JString(name)
}

case class DurationGranularity(duration: FiniteDuration) extends Granularity {
  def toJson = JObject(
    "type" -> "duration",
    "duration" -> duration.toMillis
  )
}

object Granularity {
  val All       = SimpleGranularity("all")
  val Second    = SimpleGranularity("second")
  val Minute    = SimpleGranularity("minute")
  val FifteenMinute = SimpleGranularity("fifteen_minute")
  val ThirtyMinute = SimpleGranularity("thirty_minute")
  val Day       = SimpleGranularity("day")
  val Hour      = SimpleGranularity("hour")

  val FiveMinute = DurationGranularity(5.minutes)
}
