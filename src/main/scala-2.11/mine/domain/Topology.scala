package mine.domain

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

case class Squares(width: Int, height: Int) extends Topology[(Int, Int)] {

  override val indexes = {
    for {
      x <- 0 until width
      y <- 0 until height
    } yield (x, y)
  }.par.toSet

  override def surrounding(pos: (Int, Int)) = {
    val (centreX, centreY) = pos
    for {
      x <- centreX - 1 to centreX + 1
      if x >= 0
      if x < width
      y <- centreY - 1 to centreY + 1
      if y >= 0
      if y < height
      if centreX != x || centreY != y
    } yield (x, y)
  }.par.toSet

  override def adjacent(a: (Int, Int), b: (Int, Int)) = {
    val (ax, ay) = a
    val (bx, by) = b

    math.abs(ax - bx) <= 1 && math.abs(ay - by) <= 1
  }

  override val size = width * height
}