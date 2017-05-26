package abel.aggregates

import spray.json.JsValue

trait Aggregate[T <: Aggregate[_]] { self: T =>
  def plus(another: T): T

  def show: JsValue
}

