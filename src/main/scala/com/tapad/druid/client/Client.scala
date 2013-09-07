package com.tapad.druid.client

import org.joda.time.{DateTime, DateMidnight, Interval}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson._
import org.json4s.jackson.JsonMethods._
import com.ning.http.client.Response
import org.joda.time.format.ISODateTimeFormat
import scala.util.{Failure, Success}

object Client {

  def main(args: Array[String]) {
    import dispatch._, Defaults._
    val svc = url("http://bidder04.prd.nj1.tapad.com:8082/druid/v2/?pretty")
    import DSL._
    val js = TimeSeries(
      source = "taps",
      interval = new Interval(new DateTime(2013, 06, 01, 0, 0), new DateTime(2014, 01, 03, 0, 0)),
      granularity = Granularity.Day,
      aggregate = Seq(
        sum("clicks") as "clicks",
        sum("impressions") as "impressions"
      ),
      postAggregate = Seq(
        "clicks" / "impressions" * 100 as "ctr"
      ),
      filter = "campaign_id" === "1536"
    )

    def JsonPost(req: Req, body: String) = {
      req.POST.setHeader("Content-Type", "application/json").setBody(body)
    }

    def parseJson(resp: Response): JValue = {
      parse(resp.getResponseBody("UTF-8"))
    }

    println(prettyJson(js.toJson))

    Http(JsonPost(svc, prettyJson(js.toJson)) > (parseJson _).andThen(TimeSeriesResponse.parse)).onComplete {
      case Success(s) =>
        val fmt = ISODateTimeFormat.basicDate()
        s.data.foreach {
          case (time, values) =>
            println(
              "%s\t\timps: %(,d imps\t\t %(,d clicks, \t\t%.2f ctr".format(
                fmt.print(time),
                values("impressions").asInstanceOf[BigInt],
                values("clicks").asInstanceOf[BigInt],
                values("ctr").asInstanceOf[Double]
              )
            )
        }
        println(s)
        System.exit(0)
      case Failure(ex) =>
        ex.printStackTrace()
        System.exit(0)
    }

  }
}
