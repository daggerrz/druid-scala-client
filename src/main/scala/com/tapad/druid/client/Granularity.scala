package com.tapad.druid.client

case class Granularity(name: String)
object Granularity {
  val All       = Granularity("all")
  val Second    = Granularity("second")
  val Minute    = Granularity("minute")
  val FifteenMinute = Granularity("fifteen_minute")
  val ThirtyMinute = Granularity("thirty_minute")
  val Day       = Granularity("day")
  val Hour      = Granularity("hour")
}
