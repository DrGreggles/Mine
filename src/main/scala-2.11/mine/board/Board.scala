package mine.board

import mine.board.topology.Topology
import mine.viewer.{Renderer, Viewer}

import scala.annotation.tailrec

case class Board[Pos, T <: Topology[Pos]](mask: Pos => Boolean,
                                          private val mines: Pos => Boolean,
                                          blasted: Boolean = false
                                         )(implicit val topology: T, viewer: Viewer[Pos], renderer: Renderer) {

  def init() = renderer.render(viewer.init)

  def click(clicked: Pos): Board[Pos, T] =
    if (!isHidden(clicked)) this
    else if (isMine(clicked)) {
      renderer.render(viewer.blasted(clicked, topology.indexes filter mines))
      this.copy(mask = _ => true, blasted = true)
    }
    else {
      val unmasked = squaresToRemove(Set(clicked))

      renderer.render(viewer.clicked(
        unmasked map (pos => (pos, number(pos)))
      ))
      this.copy(mask = pos => isHidden(pos) && !(unmasked contains pos))
    }

  @tailrec
  private def squaresToRemove(next: Set[Pos], previous: Set[Pos] = Set.empty[Pos]): Set[Pos] =
    next.headOption match {
      case None => previous
      case Some(pos) =>
        val surrounding = if (number(pos) == 0)
          topology.surrounding(pos)
        else if (topology.isPresent(pos))
          Set(pos)
        else Set.empty

        val newPrevious = previous + pos

        squaresToRemove((next.tail ++ surrounding) -- newPrevious, newPrevious)
    }

  def number(pos: Pos) = topology.surrounding(pos) count isMine //TODO: private with safe option method

  def complete = !topology.indexes.exists(p => isHidden(p) && !isMine(p))

  private def isMine(pos: Pos) = mines(pos)

  def isHidden(pos: Pos) = mask(pos)

  def isClickedMine(pos: Pos) = !isHidden(pos) && isMine(pos)
}

object Board {

  def apply[Pos, T <: Topology[Pos]](topology: T, mines: Int)(implicit viewer: Viewer[Pos], renderer: Renderer): Board[topology.P, T] = {

    implicit val t = topology

    val board = new Board[topology.P, T](
      mask = _ => true,
      mines = topology.chooseRandom(mines).contains,
      blasted = false
    )

    board.init()

    board
  }
}