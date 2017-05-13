package mine.domain

import scala.annotation.tailrec

case class Board[Pos, T<:Topology[Pos]]
(mask: Pos => Boolean, private val mines: Pos => Boolean, blasted: Boolean = false)
(implicit val topology: T) {

  def click(clicked: Pos): Board[Pos, T] =
    if (!isHidden(clicked)) this
    else if (isMine(clicked))
      Board[Pos, T](
        mask = (_:Pos) => true,
        mines = mines,
        blasted = true
      )
    else {
      val unmasked = squaresToRemove(Set(clicked))

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

  def number(pos: Pos) = topology.surrounding(pos) count isMine

  def complete = !topology.indexes.exists(p => isHidden(p) && !isMine(p))

  private def isMine(pos: Pos) = mines(pos)

  def isHidden(pos: Pos) = mask(pos)

  def isClickedMine(pos: Pos) = !isHidden(pos) && isMine(pos)
}

object Board {
  def apply[Pos, T<:Topology[Pos]](mines: Int)(implicit topology: T): Board[topology.P, T] = {

    new Board[topology.P, T](
      mask = _ => true,
      mines = topology.chooseRandom(mines).contains,
      blasted = false
    )(topology)
  }

  def beginner = Board[(Int, Int), Squares](10)(Squares(8, 8))

  def intermediate = Board[(Int, Int), Squares](40)(Squares(16, 16))

  def expert = Board[(Int, Int), Squares](99)(Squares(30, 16))

}