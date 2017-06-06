package mine.actor

import akka.actor.{Actor, ActorRef}
import mine.Solver
import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

class SolverActor extends Actor with Solver {

  def receive = {
    case s@Solve(attempts, newBoard) => play(newBoard, attempts)(s.viewer)
    case _ =>
  }

  private def play[Pos, T <: Topology[Pos]](newBoard: () => Board[Pos, T], maxAttempts: Int, attempt: Int = 1)(implicit viewer: Viewer[Pos]): Unit = {

    val board = newBoard()

    implicit val topology = board.topology

    println(s"Attempt: $attempt of $maxAttempts")
    solveThen(board) {
      println("Win!")
      println(s"Solved after $attempt attempts.")
    } {
      println("Blasted!")
      if (attempt < maxAttempts) play(newBoard, maxAttempts, attempt + 1)
      else println(s"Did not win after $maxAttempts attempts.")
    }
  }
}

