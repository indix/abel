package abel.serde

import java.util

import com.twitter.bijection.Injection
import com.twitter.chill.KryoInjection
import v010.kafka.common.serialization.{Deserializer, Serializer}

import scala.util.Try

object SerDe {
  def serializer[K](bi: Injection[K, Array[Byte]]) = new Serializer[K] {
    override def configure(map: util.Map[String, _], b: Boolean): Unit = {}
    override def serialize(topic: String, value: K): Array[Byte] = bi(value)
    override def close(): Unit = {}
  }

  def deserializer[K](bi : Injection[K, Array[Byte]]) = new Deserializer[K] {
    override def configure(map: util.Map[String, _], b: Boolean): Unit = {}
    override def deserialize(topic: String, bytes: Array[Byte]): K = bi.invert(bytes).get
    override def close(): Unit = {}
  }

  def kryoInjection[T] = new Injection[T, Array[Byte]] {
    override def apply(a: T): Array[Byte] = KryoInjection.apply(a)
    override def invert(b: Array[Byte]): Try[T] = KryoInjection.invert(b).map(_.asInstanceOf[T])
  }
}
