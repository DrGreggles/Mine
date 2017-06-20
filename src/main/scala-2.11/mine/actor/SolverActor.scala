package mine.actor

import akka.actor.{Actor, ActorRef}
import mine.{Known, Solver}
import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

import scala.collection.parallel.immutable.ParSet
import scala.util.Random

class SolverActor extends Actor with Solver {

  def receive = {
    case s@Solve(attempts, newBoard) => play(newBoard, attempts)(s.viewer)
    case _ =>
  }

  private def play[Pos, T <: Topology[Pos]](newBoard: () => Board[Pos, T], maxAttempts: Int, attempt: Int = 1)(implicit viewer: Viewer[Pos]): Unit = {

    val board = newBoard()

    implicit val topology = board.topology
    println(s"Attempt: $attempt of $maxAttempts")


    def win = {
      println("Win!")
      println(s"Solved after $attempt attempts.")
    }

    def lose = {
      println("Blasted!")
      if (attempt < maxAttempts) play(newBoard, maxAttempts, attempt + 1)
      else println(s"Did not win after $maxAttempts attempts.")
    }

    def move(b: Board[Pos, T], known: Known[Pos]): Unit = {

      val Known(mines, notMines) = known

      def randomPos: Pos = {
        val maybeNotMines = for {
          p <- topology.indexes
          if b.isHidden(p)
          if !(mines contains p)
        } yield p

        val n = Random.nextInt(maybeNotMines.size)
        maybeNotMines.iterator.drop(n).next
      }

      if (b.complete) win
      else if (b.blasted) lose
      else notMines.headOption match {
        case Some(pos) =>
          move(b.click(pos), known.copy(notMines = notMines - pos))
        case None =>
          val clicked = b.click(randomPos)
          move(clicked, solve(clicked, known))
      }
    }

    move(board, Known.nothing[Pos])
  }


}

case class Solve[Pos, T <: Topology[Pos]](attempts: Int, newBoard: () => Board[Pos, T])(implicit val viewer: Viewer[Pos])
