package com.tapad.druid.client

import org.joda.time.{DateTime, Interval}
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.DefaultFormats._
import org.joda.time.format.ISODateTimeFormat

case class TopNQuery(source: String,
                     interval: Interval,
                     granularity: Granularity,
                     dimension: String,
                     metric: String,
                     threshold: Int,
                     aggregate: Seq[Aggregation],
                     postAggregate: Seq[PostAggregation] = Nil,
                     filter: QueryFilter = QueryFilter.All) {
  def toJson: JValue = {
    JObject(
      "queryType" -> "topN",
      "dataSource" -> source,
      "granularity" -> granularity.toJson,
      "dimension" -> dimension,
      "threshold" -> threshold,
      "metric" -> metric,
      "aggregations" -> aggregate.map(_.toJson),
      "postAggregations" -> postAggregate.map(_.toJson),
      "intervals" -> Time.intervalToString(interval),
      "filter" -> filter.toJson
    )
  }
}

case class TopNResponse(data: Seq[(DateTime, Seq[Map[String, Any]])])
object TopNResponse {
  implicit val formats = org.json4s.DefaultFormats
  def parse(js: JValue) : TopNResponse = {
    js match {
      case JArray(results) =>
        val data = results.map { r =>
          val time = Time.parse((r \ "timestamp").extract[String])
          val results = (r \ "result").asInstanceOf[JArray]
          val valueSeq : Seq[Map[String, Any]] = results.arr.map { v =>
            v.asInstanceOf[JObject].values
          }
          time -> valueSeq
        }
        TopNResponse(data)
      case err @ _ =>
        throw new IllegalArgumentException("Invalid top N response:\n" + org.json4s.jackson.prettyJson(err))
    }
  }
}
