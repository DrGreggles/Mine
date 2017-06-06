package mine.viewer

import akka.actor.ActorRef

import scala.collection.GenSet

class Renderer(actor: ActorRef) {
  def render(items: GenSet[_ <: WindowItem]) = {
    actor ! items
  }
}
