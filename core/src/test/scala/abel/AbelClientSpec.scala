package abel

import abel.Metric.Any
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import v010.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord}

class AbelClientSpec extends FlatSpec {
  val producer = mock(classOf[KafkaProducer[String, Event]])
  val client: AbelClient = new AbelClient(producer, AbelConfig("localhost:9091","abel"))

  "Client" should "produce a producer record with the given metric" in {
    val metric = mock(classOf[Any])
    client.send(metric)

    val captor = ArgumentCaptor.forClass(classOf[ProducerRecord[String, Event]])
    val callbackCaptor = ArgumentCaptor.forClass(classOf[Callback])
    verify(producer).send(captor.capture(), callbackCaptor.capture())

    captor.getValue.topic() should be("abel")
    captor.getValue.value() should be(metric)
  }
}
