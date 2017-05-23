package mine

import java.awt.geom.{Line2D, Point2D, Rectangle2D}
import javax.swing.WindowConstants

import akka.actor.{ActorRef, ActorSystem, Props}
import mine.actor.{RendererActor, Solve, SolverActor}
import mine.board.Board
import mine.viewer.Viewer._

import scala.swing._
import scala.swing.event.MouseClicked



object Main extends App with Sweeper {

  val window = new ui.Window(solve(20))
  window.visible = true

  implicit val system = ActorSystem()

  val rendererActor = system.actorOf(Props(new RendererActor(window)), name = "renderer")
  val solverActor = system.actorOf(Props(new SolverActor(rendererActor)), name = "solver")

}

trait Sweeper {

  val solverActor: ActorRef

  def newBoard = Board.expertTorus

  val board = newBoard
  implicit val topology = board.topology

  def solve(attempts: Int) = {
    solverActor ! Solve(attempts, () => newBoard)
  }


}
