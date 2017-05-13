package mine

import mine.domain._
import mine.viewer.Viewer._

import scala.swing._


class UI extends MainFrame with Sweeper {
  title = "Topology Sweeper"
  preferredSize = new Dimension(320, 240)
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Solve")(play(20))
    }
    contents += Swing.VStrut(10)
    contents += Swing.Glue
    contents += Button("Press me, please") { println("Thank you") }
    contents += Swing.VStrut(5)
    contents += Button("Close") { sys.exit(0) }
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }
}

object Main extends App {

  val ui = new UI
  ui.visible = true
}

trait Sweeper extends Solver {

  def newBoard = Board.expert

  def play(limit: Int, attempt: Int = 1): Unit = {

    val board = newBoard

    implicit val topology = board.topology

    println(s"Attempt: $attempt of $limit")
    solveThen(board){
      println("Win!")
      println(s"Solved after $attempt attempts.")
    }{
      println("Blasted!")
      if (attempt < limit) play(limit, attempt + 1)
      else println(s"Did not win after $limit attempts.")
    }(squareViewer(topology))
  }
}
