package mine.actor

import akka.actor.Actor
import mine.ui.Window
import mine.viewer.WindowItem

import scala.collection.GenSet

class RendererActor(window: Window) extends Actor {

  override def receive = {
    case items: GenSet[WindowItem] => window render items
    case _ =>
  }

}

