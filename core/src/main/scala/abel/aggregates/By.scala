package abel.aggregates

import spray.json.JsObject

import scala.collection.immutable.HashMap

case class By[T <: Aggregate[T], G](values: HashMap[G, T with Aggregate[T]]) extends Aggregate[By[T, G]] {
  override def plus(another: By[T, G]): By[T, G] = By(values.merged(another.values) { case ((k1, v1), (k2, v2)) => (k1, v1 plus v2) })

  override def show = JsObject(values.map { case (k, v) => k.toString -> v.show })
}

object By {
  //Neat little trick for type inference to work - http://stackoverflow.com/questions/39418173/why-is-this-scala-code-not-infering-type
  def apply[T <: Aggregate[T], G](key: G, value: T with Aggregate[T]): By[T, G] = By[T, G](HashMap(key -> value))
}