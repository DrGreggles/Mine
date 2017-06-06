package mine

import akka.actor.{ActorRef, ActorSystem, Props}
import mine.actor.{RendererActor, Solve, SolverActor}
import mine.board.BoardFactory
import mine.viewer.Renderer
import mine.viewer.Viewer._



object Main extends App with Sweeper {

  val window = new ui.Window(solve(20))
  window.visible = true

  implicit val system = ActorSystem()

  val rendererActor = system.actorOf(Props(new RendererActor(window)), name = "renderer")
  val solverActor = system.actorOf(Props(new SolverActor), name = "solver")
  implicit val renderer = new Renderer(rendererActor)
  val boardFactory = new BoardFactory

}

trait Sweeper {

  val solverActor: ActorRef

  val boardFactory: BoardFactory

  def newBoard = boardFactory.smallCube

  def solve(attempts: Int): Unit = {
    solverActor ! Solve(attempts, () => newBoard)
  }

}