package abel.queue

import abel.store.MetricStore
import abel.{Event, Key, Metric, Metrics}
import v010.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

import scala.annotation.tailrec
import scala.collection.JavaConversions._

class Indexer(store: MetricStore,
              consumer: KafkaConsumer[String, Event],
              partition: String) extends Runnable {

  override def run() = try {
    process
  } finally {
    consumer.close()
  }

  private def explode(event: Event): List[Metric.Any] = event match {
    case metrics: Metrics.Any => metrics.all.asInstanceOf[List[Metric.Any]]
    case metric: Metric.Any => List(metric)
  }

  @tailrec
  private final def process: Unit = {
    val records: ConsumerRecords[String, Event] = consumer.poll(Int.MaxValue)
    var metrics: Map[Key, Metric.Any] = Map.empty

    for (record <- records;
         metric <- explode(record.value())) {
      if (metrics contains metric.key)
        metrics = metrics + (metric.key -> (metrics(metric.key) merge metric))
      else
        metrics = metrics + (metric.key -> metric)
    }

    store.mergeAll(metrics.values, partition)
    consumer.commitSync()
    process
  }
}
