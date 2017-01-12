package com.tapad.druid.client

import org.json4s._
import org.json4s.jackson._
import org.json4s.jackson.JsonMethods._
import com.ning.http.client._

import scala.concurrent.{ExecutionContext, Promise, Future}

case class DruidClient(serverUrl: String)(implicit val executionContext: ExecutionContext) {
  private val config = new AsyncHttpClientConfig.Builder()
  private val client = new AsyncHttpClient(config.build())
  private val url = s"$serverUrl/druid/v2/?pretty"

  private def JsonPost(body: String) = {
    client.preparePost(url)
      .setHeader("Content-Type", "application/json")
      .setBody(body)
  }

  private def parseJson(resp: Response): JValue = {
    val body = resp.getResponseBody("UTF-8")
    parse(body)
  }

  private def execute[R](js: JValue, parser: JValue => R) : Future[R] = {
    val p = Promise[Response]
    val body = compactJson(js)
    JsonPost(body).execute(new AsyncCompletionHandler[Response] {
      override def onCompleted(response: Response): Response = {
        p.success(response)
        response
      }
    })
    p.future.map(parseJson).map(parser)
  }

  def apply(ts: TimeSeriesQuery) : Future[TimeSeriesResponse] = execute(ts.toJson, TimeSeriesResponse.parse)
  def apply(ts: GroupByQuery) : Future[GroupByResponse] = execute(ts.toJson, GroupByResponse.parse)
  def apply(ts: TopNQuery) : Future[TopNResponse] = execute(ts.toJson, TopNResponse.parse)

  def queryTimeSeries(query: String) : Future[TimeSeriesResponse] = {
    Grammar.parser.parseAll(Grammar.parser.timeSeries, query) match {
      case Grammar.parser.Success(ts, _) => execute(ts.toJson, TimeSeriesResponse.parse)
      case failure => throw new IllegalArgumentException(failure.toString)
    }
  }

  def queryGroupBy(query: String) : Future[GroupByResponse] = {
    Grammar.parser.parseAll(Grammar.parser.groupByQuery, query) match {
      case Grammar.parser.Success(ts, _) => execute(ts.toJson, GroupByResponse.parse)
      case failure => throw new IllegalArgumentException(failure.toString)
    }
  }

  def queryTopN(query: String) : Future[TopNResponse] = {
    Grammar.parser.parseAll(Grammar.parser.topNQuery, query) match {
      case Grammar.parser.Success(ts, _) => execute(ts.toJson, TopNResponse.parse)
      case failure => throw new IllegalArgumentException(failure.toString)
    }
  }

  def close() = client.close()

}

