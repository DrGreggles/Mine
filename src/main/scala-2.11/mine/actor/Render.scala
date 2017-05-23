package mine.actor

import mine.board.Board
import mine.board.topology.Topology
import mine.viewer.Viewer

case class Render[Pos, T<:Topology[Pos]](board: Board[Pos, T], viewer: Viewer[Pos])
