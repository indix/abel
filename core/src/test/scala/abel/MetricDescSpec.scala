package abel

import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

import scala.collection.SortedSet

class MetricDescSpec extends FlatSpec {
  "SumDesc" should "explode left and right and concat them" in {
    val left = mock(classOf[MetricDesc])
    val right = mock(classOf[MetricDesc])

    when(left.explode(Key("key"))).thenReturn(List(Key("left")))
    when(right.explode(Key("key"))).thenReturn(List(Key("right")))
    SumDesc(left, right).explode(Key("key")) should be(List(Key("left"), Key("right")))

    verify(left).explode(Key("key"))
    verify(right).explode(Key("key"))
  }

  "ProdDesc" should "explode left and flatMap explode on right" in {
    val left = mock(classOf[MetricDesc])
    val right = mock(classOf[MetricDesc])

    when(left.explode(Key("key"))).thenReturn(List(Key("left1"), Key("left2")))
    when(right.explode(Key("left1"))).thenReturn(List(Key("right1"), Key("right2")))
    when(right.explode(Key("left2"))).thenReturn(List(Key("right3"), Key("right4")))
    ProdDesc(left, right).explode(Key("key")) should be(List(Key("right1"), Key("right2"), Key("right3"), Key("right4")))

    verify(left).explode(Key("key"))
    verify(right).explode(Key("left1"))
    verify(right).explode(Key("left2"))
  }

  "NothingDesc" should "be identity op on explode" in {
    NothingDesc.explode(Key("key")) should be(List(Key("key")))
  }

  "TagDesc" should "append a tag on explode" in {
    TagDesc("newtag").explode(Key("key")) should be(List(Key("key", tags = SortedSet("newtag"))))
    TagDesc("newtag").explode(Key("key", tags = SortedSet("oldtag"))) should be(List(Key("key", tags = SortedSet("newtag", "oldtag"))))
  }

  "Granularity" should "add granularity as given granularity in key" in {
    GranularityDesc(10).explode(Key("key", time = Time(101, 10))) should be(List(Key("key", time = Time(100, 10))))
    GranularityDesc(-1).explode(Key("key", time = Time(101, 10))) should be(List(Key("key", time = Time(0, -1))))
  }

  "Time" should "add time to the key" in {
    TimeDesc(101).explode(Key("key")) should be(List(Key("key", time = Time(0 , -1))))
    TimeDesc(101).explode(Key("key", time = Time(0, 10))) should be(List(Key("key", time = Time(100 , 10))))
  }
}
