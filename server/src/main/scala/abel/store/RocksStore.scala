package abel.store

import com.twitter.bijection.Injection
import org.rocksdb._


class RocksStore[V] (implicit val valueSerDe: Injection[V, Array[Byte]]) extends Store[V]{
  val options = new Options()
    .setCreateIfMissing(true)
    .setLogFileTimeToRoll(5 * 3600) // roll LOG file every 5 hours
    .setKeepLogFileNum(5)

  val db = RocksDB.open(options,"./db")
  override def get(key: String): Option[V] = Option(db.get(key.getBytes)).map((valueSerDe.invert _).andThen(_.get))

  override def put(key: String, value: V): Unit = db.put(key.getBytes, valueSerDe.apply(value))

  override def batchPut(batch:Batch[V]) = {
    val writeBatch = new WriteBatch()
    batch.items.foreach(item => writeBatch.put(item._1.getBytes, valueSerDe(item._2)))
    db.write(new WriteOptions(), writeBatch)
    writeBatch.dispose()
  }

  override def scan(startKey: String, endKey: String): CloseableIterator[(String, V)] = {
    val iterator: RocksIterator = db.newIterator()
    iterator.seek(startKey.getBytes)
    new CloseableIterator[(String, V)] {
      var closed = false
      override def hasNext: Boolean = !closed && iterator.isValid && new Predef.String(iterator.key()).compareTo(endKey) <= 0

      override def next(): (String, V) = {
        val result = (new Predef.String(iterator.key()), valueSerDe.invert(iterator.value()).get)
        iterator.next()
        result
      }
      override def close() = {
        iterator.dispose()
        closed = true
      }
    }
  }

}
