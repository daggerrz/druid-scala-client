package com.tapad.druid.client

import org.joda.time.Interval
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

case class GroupByQuery(source: String,
                        interval: Interval,
                        granularity: Granularity,
                        dimensions: Seq[String],
                        aggregate: Seq[Aggregation],
                        postAggregate: Seq[PostAggregation] = Nil,
                        filter : QueryFilter = QueryFilter.All,
                        orderBy: Seq[ColumnOrder] = Nil,
                        limit: Option[Int] = None) {
  def toJson : JValue = {
    JObject(
      "queryType" -> "groupBy",
      "dataSource" -> source,
      "granularity" -> granularity.toJson,
      "dimensions" -> dimensions,
      "aggregations" -> aggregate.map(_.toJson),
      "postAggregations" -> postAggregate.map(_.toJson),
      "intervals" -> Time.intervalToString(interval),
      "filter" -> filter.toJson,
      "orderBy" -> OrderBy(orderBy, limit).toJson
    )
  }
}

case class GroupByResponse(data: Seq[Map[String, Any]])
object GroupByResponse {
  implicit val formats = org.json4s.DefaultFormats
  def parse(js: JValue) : GroupByResponse = {
    js match {
      case JArray(results) =>
        val data = results.map { r =>
          (r \ "event").asInstanceOf[JObject].values
        }
        GroupByResponse(data)
      case err @ _ =>
        throw new IllegalArgumentException("Invalid time series response:\n" + org.json4s.jackson.prettyJson(err))
    }
  }
}
