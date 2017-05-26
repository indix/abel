package abel.store

import abel.{Time, Key}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

import scala.collection.SortedSet
import scala.util.Success

class IndexKeySpec extends FlatSpec {

  "IndexKey" should "build a string representation of it" in {
    IndexKey.keyToString(IndexKey(Key("name", SortedSet.empty, Time.Forever), "partition")) should be("name~-1~~0~partition")
    IndexKey.keyToString(IndexKey(Key("name", SortedSet("tag1", "tag2"), Time.Forever), "partition")) should be("name~-1~tag1$tag2~0~partition")
  }

  it should "build IndexKey from string representation" in {
    IndexKey.keyToString.invert("name~-1~~0~partition") should be(Success(IndexKey(Key("name", SortedSet.empty, Time.Forever), "partition")))
    IndexKey.keyToString.invert("name~-1~tag1$tag2~0~partition") should be(Success(IndexKey(Key("name", SortedSet("tag1", "tag2"), Time.Forever), "partition")))
  }


  "MetricRequest" should "build start and end key without start and end time" in {
    val metric = MetricRequest("name", List("tag1", "tag2"), None, None, None)

    metric.startKey should be("name~-1~tag1$tag2~")
    metric.endKey should be("name~-1~tag1$tag2~~~")

    metric.endKey.compareTo("name~-1~tag1$tag2~0~part1") > 0  should be(true)
    metric.startKey.compareTo("name~-1~tag1$tag2~0~part1") < 0 should be(true)
    metric.endKey.compareTo("name2~-1~tag1$tag2~0~part1") < 0 should be(false)
  }

  it should "build start and end key without tags" in {
    val metric = MetricRequest("name", List.empty, None, None, None)

    metric.startKey should be("name~-1~~")
    metric.endKey should be("name~-1~~~~")

    metric.endKey.compareTo("name~-1~~0~part1") > 0 should be(true)
    metric.startKey.compareTo("name~-1~~0~part1") < 0 should be(true)
    metric.endKey.compareTo("name~-1~tag1~0~part1") < 0 should be(false)
  }

  it should "build start and end key with start and end times" in {
    val metric = MetricRequest("name", List.empty, Some(123), Some(125), None)
    metric.startKey should be("name~-1~~123")
    metric.endKey should be("name~-1~~125~~")

    metric.startKey.compareTo("name~-1~~124~part0") < 0 should be(true)
    metric.endKey.compareTo("name~-1~~124~part0") > 0 should be(true)
    metric.endKey.compareTo("name~-1~~126~part0") > 0 should be(false)
  }
}
