package mine.domain

case class Grid[Pos, A](get: Pos => A)(implicit t: Board.Aux[Pos]) {

  def getOrElse(default: A): (Pos) => A = pos => if (t.isPresent(pos)) get(pos) else default

}