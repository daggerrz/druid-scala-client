package com.tapad.druid.client

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, Interval}

object Time {
  private final val DateTimeFormat = ISODateTimeFormat.dateTime().withOffsetParsed()
  def intervalToString(i: Interval) : String =
    "%s/%s".format(DateTimeFormat.print(i.getStart), DateTimeFormat.print(i.getEnd))

  def parse(s: String) : DateTime = DateTimeFormat.parseDateTime(s)
}
