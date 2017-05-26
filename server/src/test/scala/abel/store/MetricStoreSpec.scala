package abel.store

import abel.aggregates.Average
import abel.{Key, Metric}
import org.scalatest.FlatSpec
import org.mockito.Mockito._

class MetricStoreSpec extends FlatSpec {
  "MetricStore" should "merge each of the metric with the one in Store and push it back" in {
    val store = mock(classOf[Store[Metric.Any]])
    val metricStore = new MetricStore(store)
    when(store.get("metric1~-1~~0~partition1")).thenReturn(Some(Metric(Key("metric1"), Average(10,3))))
    when(store.get("metric2~-1~~0~partition1")).thenReturn(None)

    metricStore.mergeAll(List(Metric(Key("metric1"), Average(10, 2)), Metric(Key("metric2"), Average(10, 2))), "partition1")

    verify(store).batchPut(Batch(List("metric2~-1~~0~partition1" -> Metric(Key("metric2"),Average(10,2)),
                                      "metric1~-1~~0~partition1"-> Metric(Key("metric1"), Average(20, 5)))))
  }
}
