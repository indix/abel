package abel

import abel.store.{MetricRequest, MetricStore}
import akka.http.scaladsl.server.Directives._
import com.google.inject.Inject
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue, RootJsonWriter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class MetricResponse(responses: List[Metric.Any]) {
  def show:JsValue = JsArray(responses.map(metric =>
    JsObject(
      "key" -> JsObject(
        "name" -> JsString(metric.key.name),
        "tags" -> JsArray(metric.key.tags.map(tag => JsString(tag)).toList:_*),
        "time" -> JsNumber(metric.key.time.time),
        "duration" -> JsNumber(metric.key.time.granularity)),
      "value" -> metric.value.show)):_*)
}

class RestService @Inject() (store: MetricStore) {
  implicit object MetricResponseFormat extends RootJsonWriter[MetricResponse] {
    override def write(obj: MetricResponse): JsValue = obj.show
  }

  def routes = {
    path("metrics"){
      get {
        parameters('name, 'tags.*, 'start.as[Long]?, 'end.as[Long]?, 'duration.as[Long]? ) { (name, tags, start, end, duration) =>
          complete {
            MetricResponse(store.metricFor(MetricRequest(name, tags.toList, start, end, duration)))
          }
        }
      }
    }
  }
}
