package abel

import abel.aggregates._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import spray.json.{JsArray, JsNumber}

import scala.collection.immutable.HashMap

class AggregateSpec extends FlatSpec {

  "Count" should "sum values of 2 counts" in {
    Count(10) plus Count(20) should be(Count(30))

    Count(30).show should be(JsNumber(30))
  }

  "Average" should "find combined average of 2 given averages" in {
    (Average(10) plus Average(20)) should be(Average(30, 2))
    (Average(10, 2) plus Average(20, 3)) should be(Average(30, 5))

    Average(30, 3).show should be(JsNumber(10.0))
  }

  "UniqueCount" should "find (approximate) unique count of values" in {
    (UniqueCount("a") plus UniqueCount("b")).show should be(JsNumber(2))
    (UniqueCount("a") plus UniqueCount("a")).show should be(JsNumber(1))
  }

  "By" should "keep appropriate aggregates aggregated by key" in {
    By("key1", Count(100)) plus By("key2", Count(200)) plus By("key1", Count(200)) should be(
      By(HashMap("key1" -> Count(300), "key2" -> Count(200))))

    By("key1", Average(20, 2)) plus By("key1", Average(30, 2)) plus By("key2", Average(10, 2)) should be(
      By(HashMap("key1" -> Average(50, 4), "key2" -> Average(10, 2))))
  }


  "Pair" should "keep pair of aggregates" in {
    val pair = Pair(Count(), Average(100,1)) plus Pair(Count(10), Average(10, 10))

    pair should be(Pair(Count(11), Average(110, 11)))
    pair.show should be(JsArray(JsNumber(11), JsNumber(10.0)))
  }
}
