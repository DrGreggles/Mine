package mine.actor

import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

case class Solve[Pos, T <: Topology[Pos]](attempts: Int, newBoard: () => Board[Pos, T])(implicit val viewer: Viewer[Pos])
