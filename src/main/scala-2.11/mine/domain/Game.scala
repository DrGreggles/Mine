package mine.domain

import scala.Console._
import scala.annotation.tailrec
import scala.collection.GenSet

case class Game[Pos](mask: Grid[Pos, Boolean], private val mines: Grid[Pos, Boolean], blasted: Boolean = false)(implicit val board: Board.Aux[Pos]) {

  def click(clicked: Pos): Game[Pos] =
    if (!isHidden(clicked)) this
    else if (isMine(clicked))
      Game(
        mask = Grid(_ => true),
        mines = mines,
        blasted = true
      )
    else {
      val unmasked = squaresToRemove(Set(clicked))

      this.copy(mask = Grid(pos => isHidden(pos) && !(unmasked contains pos)))
    }

  @tailrec
  private def squaresToRemove(next: Set[Pos], previous: Set[Pos] = Set.empty[Pos]): Set[Pos] =
    next.headOption match {
      case None => previous
      case Some(pos) =>
        val surrounding = if (number(pos) == 0)
          board.surrounding(pos)
        else if (board.isPresent(pos))
          Set(pos)
        else Set.empty

        val newPrevious = previous + pos

        squaresToRemove((next.tail ++ surrounding) -- newPrevious, newPrevious)
    }

  def number(pos: Pos) = board.surrounding(pos) count isMine

  def complete = !board.indexes.exists(p => isHidden(p) && !isMine(p))

  private def isMine(pos: Pos) = mines.getOrElse(false)(pos)

  def isHidden(pos: Pos) = mask.getOrElse(false)(pos)

  def isClickedMine(pos: Pos) = !isHidden(pos) && isMine(pos)
}

object Game {
  def apply[Pos](mines: Int)(implicit board: Board.Aux[Pos]): Game[Pos] = {

    val randomMineGrid = Grid[Pos, Boolean](board.chooseRandom(mines).contains)

    Game(
      mask = Grid(_ => true),
      mines = randomMineGrid
    )
  }

  def beginner = Game(10)(Squares(8, 8))

  def intermediate = Game(40)(Squares(16, 16))

  def expert = Game(99)(Squares(30, 16))

}