package com.tapad.druid.client

object DSL {
  import Filter._
  import Aggregation._
  import PostAggregation._
  case class FilterOps(dimension: String) {
    def ===(value: String) = where(dimension, value)
    def =*=(pattern: String) = regex(dimension, pattern)
  }
  implicit def string2FilterOps(s: String) : FilterOps = FilterOps(s)

  case class PostAggOps(lhs: String) {
    def /(rhs: String) = ArithmeticPostAggregation("%s_by_%s".format(lhs, rhs), "/", Seq(FieldAccess(lhs), FieldAccess(rhs)))
    def *(rhs: String) = ArithmeticPostAggregation("%s_times_%s".format(lhs, rhs), "*", Seq(FieldAccess(lhs), FieldAccess(rhs)))
    def -(rhs: String) = ArithmeticPostAggregation("%s_minus_%s".format(lhs, rhs), "-", Seq(FieldAccess(lhs), FieldAccess(rhs)))
    def +(rhs: String) = ArithmeticPostAggregation("%s_plus_%s".format(lhs, rhs), "-", Seq(FieldAccess(lhs), FieldAccess(rhs)))
  }

  implicit def string2PostAggOps(s: String) : PostAggOps = PostAggOps(s)
  implicit def string2PostAgg(s: String) : PostAggregationFieldSpec = ArithmeticPostAggregation("no_name", "*", Seq(FieldAccess(s), constant(1)))
  implicit def numericToConstant[T](n: T)(implicit num: Numeric[T]) : ConstantPostAggregation = constant(num.toDouble(n))

  def sum(fieldName: String) = BaseAggregation("longSum", fieldName, fieldName)

}
