package mine.board

import mine.board.topology.Topology
import mine.viewer.{Renderer, Viewer}

import scala.annotation.tailrec

class Board[Pos] private(val topology: Topology.Aux[Pos])
                   (mask: Pos => Boolean,
                    mines: Pos => Boolean,
                    val blasted: Boolean = false
                   )(implicit viewer: Viewer[Pos], renderer: Renderer) {

  implicit val t = topology

  def init() = renderer.render(viewer.init)

  def click(clicked: Pos): Board[Pos] =
    if (!isHidden(clicked)) this
    else if (isMine(clicked)) {
      renderer.render(viewer.blasted(clicked, topology.indexes filter mines))

      new Board[Pos](topology)(
        mask = _ => true,
        mines = mines,
        blasted = true
      )
    }
    else {
      val unmasked = squaresToRemove(Set(clicked))

      renderer.render(viewer.clicked(
        unmasked map (pos => (pos, number(pos)))
      ))
      new Board(topology)(
        mask = pos => isHidden(pos) && !(unmasked contains pos),
        mines = mines,
        blasted = blasted
      )
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

  def apply[Pos](topology: Topology.Aux[Pos], mines: Int)(implicit viewer: Viewer[Pos], renderer: Renderer): Board[Pos] = {

    val board = new Board(topology)(
      mask = _ => true,
      mines = topology.chooseRandom(mines).contains,
      blasted = false
    )

    board.init()

    board
  }
}