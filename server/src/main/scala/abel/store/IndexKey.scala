package abel.store

import abel.{Time, Key}
import com.twitter.bijection.Injection

import scala.collection.SortedSet
import scala.util.Try

case class IndexKey(key: Key, partition: String)

case class MetricRequest(name: String, tags: List[String], startTime: Option[Long], endTime: Option[Long], duration: Option[Long]) {
  def startKey = name + "~" + duration.getOrElse(-1) + "~" + tags.sorted.mkString("$") + "~" + startTime.getOrElse("")
  def endKey = name + "~" + duration.getOrElse(-1) + "~" + tags.sorted.mkString("$") + "~" + endTime.getOrElse("") + "~~"
}

object IndexKey {
  val keyToString = new Injection[IndexKey, String] {
    override def apply(value: IndexKey): String =
      value.key.name + "~" + value.key.time.granularity + "~" + value.key.tags.mkString("$") + "~" + value.key.time.time + "~" + value.partition

    override def invert(value: String): Try[IndexKey] = {
      Try{
        val Array(name, duration, tagsString, time, partition) = value.split("~")
        IndexKey(Key(name, SortedSet(tagsString.split("\\$").filterNot(_.isEmpty):_*), Time(time.toLong, duration.toLong)), partition)
      }
    }
  }
}