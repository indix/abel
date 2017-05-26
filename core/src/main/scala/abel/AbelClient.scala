package abel

import java.net.InetAddress
import java.util.Properties

import abel.serde.SerDe._
import v010.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

case class AbelConfig(bootstrap: String, topic: String)

class AbelClient(producer: KafkaProducer[String, Event], config: AbelConfig) {
  val hostName = InetAddress.getLocalHost.getCanonicalHostName
  def send(event: Event): Unit = {
    producer.send(new ProducerRecord[String,Event](config.topic, hostName + System.currentTimeMillis(), event), new Callback {
      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
        if(e != null) e.printStackTrace()
      }
    })
  }

  def flush() = producer.flush()
}

object AbelClient {
  def apply(config: AbelConfig) = {
    lazy val props = new Properties() {
      put("bootstrap.servers", config.bootstrap)
      put("acks", "all")
    }
    val producer = new KafkaProducer[String, Event](props, serializer(kryoInjection[String]), serializer(kryoInjection[Event]))
    new AbelClient(producer, config)
  }
}
