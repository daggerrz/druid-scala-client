package com.tapad.druid.client

object DSL {
  import QueryFilter._
  import PostAggregation._
  case class FilterOps(dimension: String) {
    def ===(value: String) = where(dimension, value)
    def =*=(pattern: String) = regex(dimension, pattern)
  }
  implicit def string2FilterOps(s: String) : FilterOps = FilterOps(s)

  case class PostAggStringOps(lhs: String) {
    def /(rhs: String) = ArithmeticPostAggregation("%s_by_%s".format(lhs, rhs), "/", Seq(fieldAccess(lhs), fieldAccess(rhs)))
    def *(rhs: String) = ArithmeticPostAggregation("%s_times_%s".format(lhs, rhs), "*", Seq(fieldAccess(lhs), fieldAccess(rhs)))
    def -(rhs: String) = ArithmeticPostAggregation("%s_minus_%s".format(lhs, rhs), "-", Seq(fieldAccess(lhs), fieldAccess(rhs)))
    def +(rhs: String) = ArithmeticPostAggregation("%s_plus_%s".format(lhs, rhs), "-", Seq(fieldAccess(lhs), fieldAccess(rhs)))
  }

  implicit def string2PostAggOps(s: String) : PostAggStringOps = PostAggStringOps(s)
  implicit def string2PostAgg(s: String) : PostAggregationFieldSpec = ArithmeticPostAggregation("no_name", "*", Seq(fieldAccess(s), constant(1)))
  implicit def numericToConstant[T](n: T)(implicit num: Numeric[T]) : ConstantPostAggregation = constant(num.toDouble(n))

  case class OrderByStringOps(col: String) {
    def asc = ColumnOrder(col, "ASCENDING")
    def desc = ColumnOrder(col, "DESCENDING")
  }
  implicit def string2OrderByOps(s: String) : OrderByStringOps = OrderByStringOps(s)

  def sum(fieldName: String) = Aggregation("longSum", fieldName, fieldName + "_sum")
  def doubleSum(fieldName: String) = Aggregation("doubleSum", fieldName, fieldName + "_sum")
  def count = Aggregation("count", "na", "row_count")

}
