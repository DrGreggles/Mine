package mine.viewer

import cats.Foldable
import mine.domain.Game
import cats.implicits._

import scala.Console._

trait Viewer[Pos] {
  def print(game: Game[Pos]): Unit
}

object Viewer {


  implicit val squareViewer = new Viewer[(Int, Int)] {

    val colours = Map(
      1 -> BLUE,
      2 -> GREEN,
      3 -> RED,
      4 -> WHITE,
      5 -> YELLOW,
      6 -> CYAN,
      7 -> BLACK,
      8 -> MAGENTA
    )

    case class Square(masked: Boolean, mine: Boolean, number: Int)

    override def print(game: Game[(Int, Int)]) = {

      val xs = game.board.indexes.map(_._1)
      val ys = game.board.indexes.map(_._2)

      val minX = xs.min
      val maxX = xs.max
      val minY = ys.min
      val maxY = ys.max

      val width = maxX - minX

      def colour(s: String, colour: String, background: String = BLACK_B): String = s"$background$colour$s$RESET"

      val topBorder =
        colour(s"  ${(minX until maxX).map(_ % 10).mkString} \n", WHITE, BLACK_B) +
          colour(s" ╔${"═" * width}╗", RED, BLUE_B)

      val bottomBorder =
        colour(s" ╚${"═" * width}╝", RED, BLUE_B) +
          colour(s"\n  ${(minX until maxX).map(_ % 10).mkString} ", WHITE, BLACK_B)

      lazy val rows: IndexedSeq[List[Square]] =
        for (y <- minY until maxY) yield {
          for (x <- minX until maxX)
            yield Square(game.isHidden(x, y), game.isClickedMine(x, y), game.number(x, y))
        }.toList

      println(topBorder(width))
      rows.zipWithIndex.foreach { case (row, y) =>
        println(colour((y % 10).toString, WHITE, BLACK_B) + colour("║", RED, BLUE_B) + Foldable[List].fold {
          row.map {
            case Square(true, _, _) => colour("▒", WHITE, YELLOW_B)
            case Square(_, true, _) => colour("◉", MAGENTA)
            case Square(_, _, number) if number > 0 => colour(number.toString, colours.getOrElse(number, ""))
            case _ => colour(" ", INVISIBLE)
          }
        } + colour("║", RED, BLUE_B) + colour((y % 10).toString, WHITE, BLACK_B))
      }
      println(bottomBorder(width))
    }


  }
}