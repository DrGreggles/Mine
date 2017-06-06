package mine.viewer

import mine.viewer.Box.Unclicked

sealed trait WindowItem {
  val x: Int
  val y: Int
}

case class Box(x: Int, y: Int, state: Box.State = Unclicked, number: Int) extends WindowItem
object Box {
  sealed trait State
  case object Unclicked extends State
  case object Clicked extends State
  case object Mine extends State
  case object ClickedMine extends State
}

case class WindowDimensions(x: Int, y: Int) extends WindowItem