package com.tapad.druid.client

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.combinator.RegexParsers
import org.joda.time.{DateTime, Interval}
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormatterBuilder, DateTimeFormat}

object Grammar extends Parsers {

  /**
   * Convenience for construction filter expressions without using the full parser.
   */
  def parseFilter(filterString: String) : Either[String, QueryFilter] = {
    parser.parseAll(parser.filterExpression, filterString) match {
      case parser.Success(r, _) => Right(r.asInstanceOf[QueryFilter])
      case failure => Left(failure.toString)
    }
  }

  val parser = new RegexParsers {
    override type Elem = Char

    def keyword     = "and|or".r

    // Single and double quoted strings with double escape
    def stringLiteral: Parser[String] =
      "\"([^\"]|[\"\"]{2})*\"".r ^^ { s => s.substring(1, s.length - 1 ).replaceAll("\"\"", "\"") } |
      "\'([^\']|[\'\']{2})*\'".r ^^ { s => s.substring(1, s.length - 1 ).replaceAll("''", "'") }


    def integer     = """(0|[1-9]\d*)""".r
    def literal     = stringLiteral | integer
    def identifier   = """[_\p{L}][_\p{L}\p{Nd}]*""".r

    def selectorFilter : Parser[QueryFilter] = identifier~"="~literal ^^
      { case dim~op~value => SelectorQueryFilter(dim, value) }

    def parens      =  "\\(".r ~> filterExpression <~ "\\)".r
    def term = parens | selectorFilter

    def filterExpression : Parser[QueryFilter] = term * (
       "and" ^^^ { (e1: QueryFilter, e2: QueryFilter) => And(Seq(e1, e2))} |
       "or" ^^^ { (e1: QueryFilter, e2: QueryFilter) => Or(Seq(e1, e2))}
    )

    def whereClause : Parser[QueryFilter] = "where".r ~ filterExpression ^^ { case _ ~ filter => filter}
    
    def columnOrder : Parser[ColumnOrder] = identifier ~ (("asc".r | "desc".r)?) ^^
      { case dim~direction => ColumnOrder(dim, direction.getOrElse("desc"))}

    def groupByClause : Parser[Seq[String]] = "group by".r ~ repsep(identifier, ",") ^^ { case _ ~ cols => cols }

    def limit : Parser[Int] = "limit".r ~ integer ^^ {
      case _ ~ limit => limit.toInt
    }
    
    def orderByClause : Parser[OrderBy] = "order by".r ~ repsep(columnOrder, ",".r) ~ (limit ?) ^^ {
      case _ ~ cols ~ limit => OrderBy(cols, limit)
    } 

    def aggregationAlias : Parser[String] = "as".r ~ identifier ^^ { case _~alias => alias }
    def aggregation : Parser[Aggregation] = identifier ~ ("\\(".r ~> identifier <~ "\\)".r) ~ (aggregationAlias ?) ^^ {
      case agg~dimension~alias =>
        Aggregation(typeName = agg, fieldName = dimension, outputName = alias.getOrElse(dimension))
    }


    final val dateFormat = ISODateTimeFormat.dateOptionalTimeParser

    def absoluteDateTime : Parser[DateTime] = stringLiteral ^^ { s => dateFormat.parseDateTime(s) }
    def now : Parser[DateTime] = "now\\(\\)".r ^^^ { new DateTime() }
    def dateTime : Parser[DateTime] = (now | absoluteDateTime).withFailureMessage("Expected date literal or expression")
    def between : Parser[Interval] =  "between".r ~ dateTime ~ "and".r ~ dateTime ^^ {
      case _ ~ from ~ _ ~ to => new Interval(from, to)
    }
    def lastN : Parser[Interval] = "last".r ~ integer ~ """days|day|hours|hour|minutes|minute""".r ^^ {
      case _~amount~unit =>
        val now = new DateTime()
        val n = amount.toInt
        unit match {
          case "days" | "day"       => new Interval(now.minusDays(n), now)
          case "hours" | "hour"     => new Interval(now.minusHours(n), now)
          case "minutes" | "minute" => new Interval(now.minusMinutes(n), now)
        }
    }
    def interval : Parser[Interval] = between | lastN | "\\(".r ~> interval <~ "\\)".r

    def granularity : Parser[Granularity] = """all|1h|hourly|1min|15min|30min|1d|daily""".r ^^ {
      case "1h" | "hourly" => Granularity.Hour
      case "1min" => Granularity.Minute
      case "15min" => Granularity.FifteenMinute
      case "30min" => Granularity.ThirtyMinute
      case "1d" | "daily" => Granularity.Day
      case "all" => Granularity.All
    }
    
    def timeSeries : Parser[TimeSeriesQuery] =
         granularity ~
         interval ~ "select".r ~
        repsep(aggregation, ",") ~
        "from" ~ identifier ~(whereClause?)  ^^ {
      case granularity ~ interval ~ _ ~ aggregates ~ _ ~ source ~ filter =>
      TimeSeriesQuery(
        source = source,
        interval = interval,
        granularity = granularity,
        aggregate = aggregates,
        postAggregate = Nil,
        filter = filter.getOrElse(QueryFilter.All)
      )
    }

    def groupByQuery : Parser[GroupByQuery] =
        granularity ~
        interval ~ "select" ~
        repsep(aggregation, ",") ~
        "from" ~ identifier ~(whereClause?) ~ (groupByClause?) ^^ {
        case granularity ~ interval ~ _ ~ aggregates ~ _ ~ source ~ filter ~ groupBy =>
          GroupByQuery(
            source = source,
            dimensions = groupBy.getOrElse(Nil),
            interval = interval,
            granularity = granularity,
            aggregate = aggregates,
            postAggregate = Nil,
            filter = filter.getOrElse(QueryFilter.All)
          )
      }


  def topNQuery : Parser[TopNQuery] =
    granularity ~
      interval ~ "select" ~ "top" ~ integer ~ identifier ~ "," ~
      repsep(aggregation, ",") ~
      "from" ~ identifier ~(whereClause?) ~ "order by".r ~ identifier ^^ {
      case granularity ~ interval ~ _ ~  _ ~ threshold ~ identifier ~ _ ~ aggregates ~ _ ~ source ~ filter ~ _ ~ metric =>
        TopNQuery(
          source = source,
          dimension = identifier,
          threshold = threshold.toInt,
          metric = metric,
          interval = interval,
          granularity = granularity,
          aggregate = aggregates,
          postAggregate = Nil,
          filter = filter.getOrElse(QueryFilter.All)
        )
    }
  }

}
