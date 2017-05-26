package abel.aggregates

import spray.json.JsNumber

case class Count(value: Long = 1) extends Aggregate[Count] {
  def plus(another: Count) = Count(value + another.value)

  def show = JsNumber(value)
}