package abel.aggregates

import spray.json.JsArray

case class Pair[F <: Aggregate[F], S <: Aggregate[S]](f: F, s: S) extends Aggregate[Pair[F,S]] {
  override def plus(another: Pair[F, S]): Pair[F, S] = Pair[F,S](f plus another.f, s plus another.s)

  override def show = JsArray(f.show, s.show)
}
