package mine

import mine.domain.Game
import mine.viewer.Viewer

import scala.annotation.tailrec
import scala.collection.GenSet
import scala.collection.parallel.ParSet
import scala.util.Random

trait Solver {

  type Pos
  var losses: Int
  var wins: Int

  @tailrec
  final def solve(game: Game[Pos], decipheredMines: GenSet[Pos], notMines: GenSet[Pos] = ParSet.empty)(implicit viewer: Viewer[Pos]): Unit = {

    import game._
//    viewer print game

    if (blasted) {
      println(s"***BLASTED***")
      losses += 1
    } else if (complete) {
      println("WIN!!!!!!!!!!!!!!!!!!!!!!")
      wins += 1
    } else if (notMines.nonEmpty) {

      val hidden = notMines filter isHidden

      hidden.headOption match {
        case Some(pos) =>
          solve(click(pos), decipheredMines, hidden.tail)
        case _ =>
          solve(game, decipheredMines, hidden)
      }

    } else {
      def randomPos: Pos = {
        val maybeNotMines = for {
          p <- board.indexes
          if isHidden(p)
          if !(decipheredMines contains p)
        } yield p

        val n = Random.nextInt(maybeNotMines.size)
        maybeNotMines.iterator.drop(n).next
      }

      def minesIn(set: GenSet[Pos]): Int = (set intersect decipheredMines).size

      def hiddenIn(next: GenSet[Pos]): Int = next count isHidden

      def knowMinesNextTo(p: Pos): Boolean = minesIn(board.surrounding(p)) >= number(p)

      def hiddenNextToSameAsNumber(p: Pos) = hiddenIn(board.surrounding(p)) == number(p)


      lazy val newMines = for {
        p <- board.indexes
        if !isHidden(p)
        if hiddenNextToSameAsNumber(p)
        minePos <- board.surrounding(p)
        if isHidden(minePos)
      } yield minePos

      val newNotMines = for {
        p <- board.indexes
        if !isHidden(p)
        if number(p) > 0
        if knowMinesNextTo(p)
        (adjPos) <- board.surrounding(p)
        if isHidden(adjPos)
        if !(decipheredMines contains adjPos)
      } yield adjPos

      def containsMines(nearPos: Pos) = {
        val near = board.surrounding(nearPos) flatMap {
          p => board.surrounding(p)
        }

        for {
          p <- near
          if !isHidden(p)
          n = number(p)
          if n > 0
        } yield (board.surrounding(p) filter isHidden, n, p)
      }

      lazy val alsoNotMines = for {
        p <- board.indexes
        if !isHidden(p)
        num = number(p)
        (hasMines, n, pos) <- containsMines(p)
        if p != pos
        intersection = hasMines intersect board.surrounding(p)
        if n - (hasMines.size - intersection.size) >= num
        adj <- board.surrounding(p) diff intersection
        if isHidden(adj)
      } yield adj

      if (newNotMines.nonEmpty)
        solve(game, decipheredMines, newNotMines)
      else if (alsoNotMines.nonEmpty)
        solve(game, decipheredMines, alsoNotMines)
      else if (newMines.size > decipheredMines.size)
        solve(game, newMines)
      else
        solve(click(randomPos), newMines)

    }
  }
}
