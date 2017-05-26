package abel.queue

import java.util.Properties

import abel.{Event, Settings}
import com.google.inject.Inject
import com.google.inject.name.Named
import v010.kafka.clients.consumer.KafkaConsumer
import v010.kafka.common.serialization.Deserializer

import scala.collection.JavaConversions._

class KafkaConsumerFactory @Inject() (settings: Settings,
                                      @Named("kafka-key-deserializer") keyDeserializer: Deserializer[String],
                                      @Named("kafka-value-deserializer") valueDeserializer: Deserializer[Event]) {

  lazy val options = new Properties(){
    put("zookeeper.connect", settings.zookeeper)
    put("group.id", settings.group)
    put("bootstrap.servers", settings.bootstrap)
    put("auto.commit.enable", "false")
    put("auto.offset.reset", "earliest")
    put("offsets.storage", "kafka")
    put("dual.commit.enabled", "true")
  }

  def newConsumer(seekToBeginning: Boolean = false) = {
    val consumer = new KafkaConsumer(options, keyDeserializer, valueDeserializer)
    consumer.subscribe(List(settings.topic))
    if(seekToBeginning) consumer.seekToBeginning(consumer.assignment())
    consumer
  }
}
