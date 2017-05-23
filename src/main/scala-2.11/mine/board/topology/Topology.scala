package mine.board.topology

import scala.collection.GenSet
import scala.util.Random

trait Topology[Pos] {

  type P = Pos

  //Square indexes
  val indexes: GenSet[Pos]

  //Set of all possible indexes
  def surrounding(pos: Pos): GenSet[Pos] //Set of squares surrounding a square (not inclusive)

  def size: Int = indexes.size //total size

  def adjacent(a: Pos, b: Pos): Boolean = surrounding(a).aggregate(false)(
    { case (prev, p) => prev || p == b }, { case (e, f) => e || f }
  )

  def isPresent(pos: Pos) = indexes contains pos

  def chooseRandom(n: Int): GenSet[Pos] = Random.shuffle(indexes.toList).take(n).par.toSet
}
