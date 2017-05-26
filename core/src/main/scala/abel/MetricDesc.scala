package abel

import abel.Time._
import abel.aggregates.UniqueCount

import scala.concurrent.duration.{DurationLong, FiniteDuration}

trait MetricDesc {
  def |(another: MetricDesc) = SumDesc(this, another)
  def *(another: MetricDesc) = ProdDesc(this, another)
  def explode(keys: Key): List[Key]
}

case class SumDesc(left: MetricDesc, right: MetricDesc) extends MetricDesc {
  override def explode(key: Key): List[Key] = left.explode(key) ++ right.explode(key)
}
case class ProdDesc(left: MetricDesc, right: MetricDesc) extends MetricDesc {
  override def explode(key: Key): List[Key] = left.explode(key).flatMap(right explode)
}
case object NothingDesc extends MetricDesc {
  override def explode(key: Key): List[Key] = List(key)
}
case class TagDesc(name: String) extends MetricDesc {
  override def explode(key: Key): List[Key] = List(key.copy(tags = key.tags + name))
}
case class GranularityDesc(granularity: Long) extends MetricDesc {
  override def explode(key: Key): List[Key] = List(key.copy(time = Granularity(granularity)(key.time.time)))
}
case class TimeDesc(time: Long) extends MetricDesc {
  override def explode(key: Key): List[Key] = List(key.copy(time = Granularity(key.time.granularity)(time)))
}

object MetricDesc {
  def tag(name: String) = TagDesc(name)
  def now = TimeDesc(System.currentTimeMillis())
  def `#` = NothingDesc

  implicit def granularity(dur: FiniteDuration) = GranularityDesc(dur.toMillis)
  def perhour: GranularityDesc = 1 hour
  def perday: GranularityDesc = 1 day
  def forever: GranularityDesc = GranularityDesc(-1)
}
