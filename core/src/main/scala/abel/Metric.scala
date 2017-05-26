package abel

import abel.aggregates.Aggregate

import scala.collection.SortedSet
import scala.concurrent.duration.{DurationInt, FiniteDuration}

protected case class Time(time: Long, granularity: Long)

object Time {
  val Forever = Time(0, -1)
  def Granularity(duration: FiniteDuration)(now: Long = System.currentTimeMillis()):Time= Granularity(duration.toMillis)(now)
  def Granularity(duration: Long)(now: Long):Time = {
    if(duration == -1) Time(0, -1)
    else if(now == 0) Time(System.currentTimeMillis() / duration * duration, duration)
    else Time(now / duration * duration, duration)
  }
  def apply(time: Long, granularity: FiniteDuration):Time = Granularity(granularity)(time)
  def Today = Granularity(1 day)(System.currentTimeMillis())
  def ThisHour = Granularity(1 hour)(System.currentTimeMillis())
  def ThisMinute = Granularity(1 minute)(System.currentTimeMillis())
}

trait Event
case class Key(name:String, tags:SortedSet[String] = SortedSet.empty, time:Time = Time.Forever)

case class Metric[T <: Aggregate[T]] (key: Key, value: T with Aggregate[T]) extends Event {
  type Aggr = T with Aggregate[T]
  def merge:PartialFunction[Metric.Any, Metric.Any] = {
    // Not sure why the type cast is necessary but it cribs
    case other:Metric[T] if other.key == this.key  => Metric[T](key, value.plus(other.asInstanceOf[Metric[Aggr]].value).asInstanceOf[Aggr])
  }
}

object Metric {
  type Any = Metric[M] forSome {type M <: Aggregate[M]}
}

case class Metrics[T <: Aggregate[T]](name:String, desc: MetricDesc, value: T with Aggregate[T]) extends Event {
  def all = desc.explode(Key(name)).map(Metric(_, value))
}

object Metrics {
  type Any = Metrics[M] forSome {type M <: Aggregate[M]}
}
