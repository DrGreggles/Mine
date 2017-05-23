package mine.actor

import akka.actor.Actor
import mine.ui.Window

class RendererActor(window: Window) extends Actor {

  override def receive = {
    case Render(board, viewer) => window render viewer.items(board)
    case _ =>
  }

}

