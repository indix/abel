package abel.store

import abel.Metric
import com.google.inject.Inject

class MetricStore @Inject() (store: Store[Metric.Any]) {

  def mergeAll(metrics: Iterable[Metric.Any], partition: String) = {
    var batch = List.empty[(String, Metric.Any)]
    for (metric <- metrics) {

      val indexKey = IndexKey.keyToString(IndexKey(metric.key, partition))
      val left = store.get(indexKey)
      if (left isDefined) batch = (indexKey -> left.get.merge(metric)) :: batch
      else batch = (indexKey -> metric) :: batch
    }
    store.batchPut(Batch(batch))
  }

  def metricFor(request: MetricRequest): List[Metric.Any] = {
    val items = store.scan(request.startKey, request.endKey)
    try {
      items.toList
        .map(_._2)
        .groupBy(_.key)
        .map { case (key, values) => values.reduce(_ merge _) }
        .toList
    } finally {
      items.close()
    }
  }
}
