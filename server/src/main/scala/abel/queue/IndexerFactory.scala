package abel.queue

import java.util.UUID

import abel.Settings
import abel.store.MetricStore
import com.google.inject.Inject

class IndexerFactory @Inject() (settings: Settings,
                                kafkaConsumerFactory: KafkaConsumerFactory,
                                metricStore: MetricStore) {

  var index = 1
  lazy val uuid =  UUID.randomUUID().toString
  def startNewIndexer = {
    val consumer = kafkaConsumerFactory.newConsumer()
    val indexer = new Indexer(metricStore, consumer, uuid + index)
    index = index + 1
    new Thread(indexer).start()
  }
}
