package mine

import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

import scala.annotation.tailrec
import scala.collection.GenSet
import scala.collection.parallel.ParSet
import scala.util.Random

trait Solver {

  def render[Pos, T<: Topology[Pos]](board: Board[Pos, T])(implicit viewer: Viewer[Pos])

  final def solveThen[Pos, T<: Topology[Pos]](initialGame: Board[Pos, T])
                          (win: => Unit)
                          (lose: => Unit)(implicit viewer: Viewer[Pos]): Unit = {

    @tailrec
    def solve(board: Board[Pos, T])(decipheredMines: GenSet[Pos] = ParSet.empty, notMines: GenSet[Pos] = ParSet.empty): Unit = {
      import board._

      render(board)

      if (board.blasted) lose
      else if (complete) win
      else if (notMines.nonEmpty) {
        val hidden = notMines filter isHidden
        hidden.headOption match {
          case Some(pos) =>
            solve(click(pos))(decipheredMines, hidden.tail)
          case _ =>
            solve(board)(decipheredMines, hidden)
        }
      } else {
        def randomPos: Pos = {
          val maybeNotMines = for {
            p <- topology.indexes
            if isHidden(p)
            if !(decipheredMines contains p)
          } yield p

          val n = Random.nextInt(maybeNotMines.size)
          maybeNotMines.iterator.drop(n).next
        }

        def minesIn(set: GenSet[Pos]): Int = (set intersect decipheredMines).size

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
          if !(decipheredMines contains adjPos)
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
          solve(board)(decipheredMines, newNotMines)
        else if (alsoNotMines.nonEmpty)
          solve(board)(decipheredMines, alsoNotMines)
        else if (newMines.size > decipheredMines.size)
          solve(board)(newMines)
        else
          solve(click(randomPos))(newMines)

      }
    }

    solve(initialGame)()
  }
}