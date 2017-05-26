package abel.aggregates

import spray.json.JsNumber

case class Average(total: Double, count: Long) extends Aggregate[Average] {
  def plus(another: Average) = Average(total + another.total, count + another.count)

  def show = JsNumber(total / count.toDouble)
}

object Average {
  def apply(value: Double): Average = Average(value, 1)
}

