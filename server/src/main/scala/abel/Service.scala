package abel

import abel.queue.IndexerFactory
import abel.store.MetricStore
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.typesafe.config.Config


class Settings(config: Config) {
  val zookeeper = config.getString("kafka.zookeeper")
  val group = config.getString("kafka.group")
  val topic = config.getString("kafka.topic")
  val bootstrap = config.getString("kafka.bootstrap")
  val host = config.getString("http.host")
  val port = config.getInt("http.port")
  val consumerCount = config.getInt("kafka.consumer.count")
}


object Service {
  implicit val actorSystem = ActorSystem("abel")
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new AbelModule)

    val indexerFactory = injector.getInstance(classOf[IndexerFactory])
    val settings = injector.getInstance(classOf[Settings])
    val metricStore = injector.getInstance(classOf[MetricStore])
    val restService = injector.getInstance(classOf[RestService])

    for(i <- 1.to(settings.consumerCount)) indexerFactory.startNewIndexer
    Http().bindAndHandle(Route.handlerFlow(restService.routes), settings.host, settings.port)
  }
  

}
