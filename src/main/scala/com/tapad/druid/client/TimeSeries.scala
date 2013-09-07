package com.tapad.druid.client

import org.joda.time.{DateTime, Interval}
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.DefaultFormats._
import org.joda.time.format.ISODateTimeFormat


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

case class TimeSeries(source: String,
                      interval: Interval,
                      granularity: Granularity,
                      aggregate: Seq[Aggregation],
                      postAggregate: Seq[PostAggregation] = Nil,
                      filter : Filter = Filter.All) {
  def toJson : JValue = {
    JObject(
      "queryType" -> "timeseries",
      "dataSource" -> source,
      "granularity" -> granularity.name,
      "aggregations" -> aggregate.map(_.toJson),
      "postAggregations" -> postAggregate.map(_.toJson),
      "intervals" -> Time.intervalToString(interval),
      "filter" -> filter.toJson
    )
  }
}

case class TimeSeriesResponse(data: Seq[(DateTime, Map[String, Any])])
object TimeSeriesResponse {
  implicit val formats = org.json4s.DefaultFormats
  def parse(js: JValue) : TimeSeriesResponse = {
    js match {
      case JArray(results) =>
        val data = results.map { r =>
          val time = Time.parse((r \ "timestamp").extract[String])
          val values = (r \ "result").asInstanceOf[JObject].values
          time -> values
        }
        TimeSeriesResponse(data)
      case err @ _ =>
        throw new IllegalArgumentException("Invalid time series response: " + err)
    }
  }
}
