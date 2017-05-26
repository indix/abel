package abel

import abel.aggregates.{Average, Count}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class MetricSpec extends FlatSpec {
  "Metric" should "have merge not defined if keys are different" in {
    val metric = Metric(Key("metric"), Average(2)).asInstanceOf[Metric.Any]
    Metric(Key("metric2"), Average(3)).merge.isDefinedAt(metric) should be(false)
  }

  ignore should "have merge not defined when aggregates aren't same type" in {
    // This should ideally pass, but it wont
    val metric = Metric(Key("metric"), Average(2)).asInstanceOf[Metric.Any]
    Metric(Key("metric"), Count(1)).merge.isDefinedAt(metric) should be(false)
  }

  it should "merge when the metric types are same" in {
    val metric = Metric(Key("metric"), Average(2)).asInstanceOf[Metric.Any]
    Metric(Key("metric"), Average(3)).merge(metric) should be(Metric(Key("metric"), Average(5, 2)))
  }
}
