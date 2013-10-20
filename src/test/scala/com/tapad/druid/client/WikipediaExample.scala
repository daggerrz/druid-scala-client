package com.tapad.druid.client

import scala.concurrent.ExecutionContext
import org.joda.time.{DateTime, Interval}
import scala.util.{Failure, Success}

object WikipediaExample {

  def main(args: Array[String]) {
    implicit val executionContext = ExecutionContext.Implicits.global
    val client = DruidClient("http://druid01.prd.nj1.tapad.com:8083")

    import com.tapad.druid.client.DSL._
    val query = GroupByQuery(
      source = "tap",
      interval = new Interval(new DateTime().minusMonths(12), new DateTime()),
      dimensions = Seq("user_agent"),
      granularity = Granularity.All,
      aggregate = Seq(
        sum("count") as "user_agent_count"
      ),
      postAggregate = Seq(
//        "chars_added" / "edits" as "chars_per_edit"
      ),
//      filter = "namespace" === "article" and "country" === "United States",
//      orderBy = Seq(
//        "chars_added" desc
//      ),
      limit = Some(100)
    )

    client(query).onComplete {
      case Success(resp) =>
        resp.data.foreach { row =>
          println(row)
//          println("Page %s, %s edits, %s chars added, %s per edit".format(
//            row("page"), row("edits"), row("chars_added"), row("chars_per_edit")
//          ))
        }
        System.exit(0)
      case Failure(ex) =>
        ex.printStackTrace()
        System.exit(0)
    }

  }
}
