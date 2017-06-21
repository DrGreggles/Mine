package mine

import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

import scala.annotation.tailrec
import scala.collection.GenSet
import scala.collection.parallel.ParSet
import scala.util.Random

trait Solver {

  @tailrec
  final def solve[Pos](board: Board[Pos], known: Known[Pos]): Known[Pos] = {
    import board._

    val Known(knownMines, notMines) = known

    if (notMines.nonEmpty) {
      val hiddenNonMines = notMines filter isHidden

      if (hiddenNonMines.nonEmpty)
        Known(knownMines, hiddenNonMines)
      else
        solve(board, Known(knownMines, hiddenNonMines))

    } else {

      def minesIn(set: GenSet[Pos]): Int = (set intersect knownMines).size

      def hiddenIn(next: GenSet[Pos]): Int = next count isHidden

      def knowMinesNextTo(p: Pos): Boolean = minesIn(topology.surrounding(p)) >= number(p)

      def allSurroundingHiddenAreMines(p: Pos) = hiddenIn(topology.surrounding(p)) == number(p)


      lazy val newMines = for {
        p <- topology.indexes
        if !isHidden(p)
        if allSurroundingHiddenAreMines(p)
        minePos <- topology.surrounding(p)
        if isHidden(minePos)
      } yield minePos

      val newNotMines = for {
        p <- topology.indexes
        if !isHidden(p)
        if number(p) > 0
        if knowMinesNextTo(p)
        (adjPos) <- topology.surrounding(p)
        if isHidden(adjPos)
        if !(knownMines contains adjPos)
      } yield adjPos

      def containsMines(nearPos: Pos) = {
        val near = topology.surrounding(nearPos) flatMap {
          p => topology.surrounding(p)
        }

        for {
          p <- near
          if !isHidden(p)
          n = number(p)
          if n > 0
        } yield (topology.surrounding(p) filter isHidden, n, p)
      }

      lazy val alsoNotMines = for {
        p <- topology.indexes
        if !isHidden(p)
        num = number(p)
        (hasMines, n, pos) <- containsMines(p)
        if p != pos
        intersection = hasMines intersect topology.surrounding(p)
        if n - (hasMines.size - intersection.size) >= num
        adj <- topology.surrounding(p) diff intersection
        if isHidden(adj)
      } yield adj

      if (newNotMines.nonEmpty)
        solve(board, Known(knownMines, newNotMines))
      else if (alsoNotMines.nonEmpty)
        solve(board, Known(knownMines, alsoNotMines))
      else if (newMines.size > knownMines.size)
        solve(board, known.copy(mines = newMines))
      else
        known.copy(mines = newMines)

    }
  }
}

case class Known[Pos](mines: GenSet[Pos], notMines: GenSet[Pos])

object Known {
  def nothing[Pos] = Known(ParSet.empty[Pos], ParSet.empty[Pos])
}