package abel.aggregates

import com.twitter.algebird.{HyperLogLogMonoid, HLL}
import spray.json.JsNumber

trait UniqueCount extends Aggregate[UniqueCount]

case class UniqueCount1(value:String, bits: Int) extends UniqueCount {
  def plus(another: UniqueCount) = another match {
    case other: UniqueCount1  => UniqueCountN(this.toHLL + other.toHLL)
    case UniqueCountN(otherHLL) => UniqueCountN(this.toHLL + otherHLL)
  }

  def toHLL = {
    val monoid: HyperLogLogMonoid = new HyperLogLogMonoid(bits)
    monoid.create(value.getBytes)
  }

  def show = JsNumber(1)
}

case class UniqueCountN(hll: HLL) extends UniqueCount {
  def plus(another: UniqueCount) = another match {
    case UniqueCountN(anotherHll) => UniqueCountN(hll + anotherHll)
    case other:UniqueCount1 => UniqueCountN(other.toHLL + hll)
  }

  def show = JsNumber(hll.approximateSize.estimate)
}

object UniqueCount {
  def apply(value: String, bits: Int = 14): UniqueCount = UniqueCount1(value, bits)
}