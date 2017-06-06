package mine.actor

import mine.board.topology.Topology
import mine.viewer.WindowItem

import scala.collection.GenSet

case class Render[Pos, T<:Topology[Pos]](items: GenSet[WindowItem])
