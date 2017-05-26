package abel.store

case class Batch[V](items:List[(String,V)])

trait CloseableIterator[T] extends Iterator[T] with AutoCloseable
trait Store[V] {
  def get(key:String):Option[V]
  def put(key:String, value:V):Unit
  def batchPut(batch:Batch[V]) = batch.items.foreach(item => put(item._1, item._2))
  def scan(startKey: String, endKey: String): CloseableIterator[(String, V)]
}
