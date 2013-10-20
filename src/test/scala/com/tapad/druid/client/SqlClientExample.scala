package com.tapad.druid.client

import org.joda.time.{DateTime, Interval}

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

object SqlClientExample {

  def main(args: Array[String]) {
    implicit val executionContext = ExecutionContext.Implicits.global
    val client = DruidClient("http://druid01.prd.nj1.tapad.com:8082")

    import com.tapad.druid.client.DSL._

    client.queryTopN(
      "daily between '2015-12-03' and '2015-12-04' " +
        "select top 10 action_id, longSum(count) from tap where " +
        "campaign_id = 4383 order by count"
    ).onComplete {
      case Success(data) => data.data.foreach { println }
    }

//    client.queryTimeSeries(
//      "hourly between '2015-11-18T00:00:00.000-05:00' and '2015-11-26' " +
//        "select longSum(count), hyperUnique(unique_device_count) from tap where " +
//        "tactic_id = 129910 and " +
//        "action_id = 'impression'"
//    ).onComplete {
//      case Success(data) => data.data.foreach { case (ts, values) => println(s"$ts ${values("unique_device_count")}") }
//    }

  }
}
