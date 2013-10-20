package com.tapad.druid.client

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.{DateTime, Interval}
import org.joda.time.format.DateTimeFormat

class IntervalGrammarSpec extends FlatSpec with Matchers {
  import Grammar._
  def date(s: String) = DateTimeFormat.forPattern("YYYY-MM-dd").parseDateTime(s)

  "The interval parser" should "parse intervals" in {
    def interval(s: String) : Interval = parser.parseAll(parser.interval, s) match {
      case parser.Success(r, _) => r.asInstanceOf[Interval]
      case x => fail(x.toString)
    }
    val i = interval("between '2013-01-01' and now()")
    i.getStart should be(date("2013-01-01"))
    i.getEnd.withTimeAtStartOfDay should be(new DateTime().withTimeAtStartOfDay)
  }

}
