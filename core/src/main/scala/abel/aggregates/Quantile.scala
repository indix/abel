package abel.aggregates
import org.isarnproject.sketches.TDigest
import spray.json.{JsNumber, JsObject, JsValue}

trait Quantile extends Aggregate[Quantile]

case class Quantile1(value: Double) extends Quantile{
  override def plus(another: Quantile): Quantile = another match {
    case other:Quantile1 => QuantileN(TDigest.combine(this.toTDigest, other.toTDigest))
    case QuantileN(otherTDigest) => QuantileN(TDigest.combine(this.toTDigest,otherTDigest))
  }
  def toTDigest = {
    TDigest.sketch(List(this.value))
  }
  override def show: JsValue = JsObject("50" -> JsNumber(value),
    "75" -> JsNumber(value),
    "90" -> JsNumber(value),
    "95" -> JsNumber(value),
    "99" -> JsNumber(value),
    "99.9" -> JsNumber(value))
}

case class QuantileN(tDigest: TDigest) extends Quantile {
  override def plus(another: Quantile): Quantile = another match {
    case QuantileN(otherTDigest) => QuantileN(TDigest.combine(tDigest, otherTDigest))
    case other:Quantile1 => QuantileN(TDigest.combine(other.toTDigest,tDigest))
  }

  override def show: JsValue = JsObject("50" -> JsNumber(tDigest.cdfInverse(0.5)),
                                        "75" -> JsNumber(tDigest.cdfInverse(0.75)),
                                        "90" -> JsNumber(tDigest.cdfInverse(0.9)),
                                        "95" -> JsNumber(tDigest.cdfInverse(0.95)),
                                        "99" -> JsNumber(tDigest.cdfInverse(0.99)),
                                        "99.9" -> JsNumber(tDigest.cdfInverse(0.999)))
}
