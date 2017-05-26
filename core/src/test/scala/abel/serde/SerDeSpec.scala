package abel.serde

import com.twitter.bijection.Injection
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

import scala.util.Success

class SerDeSpec extends FlatSpec {
  "SerDe" should "convert an Injection into a Serializer" in {
    val injection = mock(classOf[Injection[String, Array[Byte]]])
    val serializer = SerDe.serializer(injection)
    val bytes = Array[Byte]()

    when(injection.apply("value")).thenReturn(bytes)

    serializer.serialize("topic", "value") should be(bytes)

    verify(injection).apply("value")
  }

  it should "convert an Injection into a Deserializer" in {
    val injection = mock(classOf[Injection[String, Array[Byte]]])
    val serializer = SerDe.deserializer(injection)
    val bytes = Array[Byte]()

    when(injection.invert(bytes)).thenReturn(Success("value"))

    serializer.deserialize("topic", bytes) should be("value")

    verify(injection).invert(bytes)
  }
}
