package abel

import abel.serde.SerDe._
import abel.store.{RocksStore, Store}
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, TypeLiteral}
import com.typesafe.config.ConfigFactory
import v010.kafka.common.serialization.Deserializer

class AbelModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Settings]).toInstance(new Settings(ConfigFactory.load()))
    bind(new TypeLiteral[Store[Metric.Any]]() {}).toInstance(new RocksStore[Metric.Any]()(kryoInjection))
    bind(new TypeLiteral[Deserializer[String]]() {}).annotatedWith(Names.named("kafka-key-deserializer")).toInstance(deserializer(kryoInjection))
    bind(new TypeLiteral[Deserializer[Event]]() {}).annotatedWith(Names.named("kafka-value-deserializer")).toInstance(deserializer(kryoInjection))
  }
}
