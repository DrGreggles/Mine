package mine.viewer

import mine.board.topology.Topology

import scala.collection.GenSet

abstract class Viewer[Pos] {

  protected def posToCoord(pos: Pos)(implicit topology: Topology[Pos]): (Int, Int)

  def init(implicit topology: Topology[Pos]): GenSet[_ <: WindowItem] = {
    val boxes = topology.indexes.toList map boxFromPos(Box.Unclicked)
    val dimensions = WindowDimensions(boxes.map(_.x).max + 1, boxes.map(_.y).max + 1)
    (dimensions :: boxes).toSet
  }

  def clicked(clicked: GenSet[(Pos, Int)])(implicit topology: Topology[Pos]): GenSet[_ <: WindowItem] = clicked map {
    case (pos, number) => boxFromPos(Box.Clicked, number)(pos)
  }

  def blasted(clickedMine: Pos, mines: GenSet[Pos])(implicit topology: Topology[Pos]): GenSet[_ <: WindowItem] = {

    val mineBoxes = (mines - clickedMine) map boxFromPos(Box.Mine)
    mineBoxes + boxFromPos(Box.Mine)(clickedMine)
  }

  private def boxFromPos(state: Box.State, number: Int = 0)(pos: Pos)(implicit topology: Topology[Pos]): Box = {
    val (x, y) = posToCoord(pos)
    Box(x, y, state, number)
  }
}

object Viewer {

  implicit def twoDimensionalViewer = new Viewer[(Int, Int)] {
    override def posToCoord(pos: (Int, Int))(implicit topology: Topology[(Int, Int)]): (Int, Int) = pos
  }

  implicit def nDimensionalViewer = new Viewer[List[Int]] {
    override def posToCoord(pos: List[Int])(implicit topology: Topology[List[Int]]): (Int, Int) = {

      def dimensionOffset(currentX: Int,
                          currentY: Int,
                          xs: List[Int],
                          index: Int = 0,
                          currentXoffset: Int = 0,
                          currentYoffset: Int = 0,
                          offsetXMultiplier: Int = 1,
                          offsetYMultiplier: Int = 1
                         ): (Int, Int) = {

        xs match {

          case coordInIndexDimension :: remaining if index % 2 == 0 =>
            val newXMultiplier = offsetXMultiplier * topology.maxDimensions(index)
            val offsetPerPlane = currentXoffset + newXMultiplier + 1
            dimensionOffset(
              currentX + coordInIndexDimension * offsetPerPlane,
              currentY,
              remaining,
              index + 1,
              offsetPerPlane,
              currentYoffset,
              newXMultiplier,
              offsetYMultiplier
            )
          case coordInIndexDimension :: remaining =>
            val newYMultiplier = offsetYMultiplier * topology.maxDimensions(index)
            val offsetPerPlane = currentYoffset + newYMultiplier + 1
            dimensionOffset(
              currentX,
              currentY + coordInIndexDimension * offsetPerPlane,
              remaining,
              index + 1,
              currentXoffset,
              offsetPerPlane,
              offsetXMultiplier,
              newYMultiplier
            )
          case Nil => (currentX, currentY)
        }
      }

      pos match {
        case x :: y :: xs => dimensionOffset(x, y, xs)
        case x :: Nil => (x, 0)
        case _ => (0, 0)
      }
    }

//    override def items[T <: Topology[List[Int]]](board: Board[List[Int], T]): GenSet[WindowItem] = {
//
//
//      def posToBoxPosition(pos: List[Int]): (Int, Int) = {
//
//        def dimensionOffset(currentX: Int,
//                            currentY: Int,
//                            xs: List[Int],
//                            index: Int = 0,
//                            currentXoffset: Int = 0,
//                            currentYoffset: Int = 0,
//                            offsetXMultiplier: Int = 1,
//                            offsetYMultiplier: Int = 1
//                           ): (Int, Int) = {
//
//          xs match {
//
//            case coordInIndexDimension :: remaining if index % 2 == 0 =>
//              val newXMultiplier = offsetXMultiplier * board.topology.maxDimensions(index)
//              val offsetPerPlane = currentXoffset + newXMultiplier + 1
//              dimensionOffset(
//                currentX + coordInIndexDimension * offsetPerPlane,
//                currentY,
//                remaining,
//                index + 1,
//                offsetPerPlane,
//                currentYoffset,
//                newXMultiplier,
//                offsetYMultiplier
//              )
//            case coordInIndexDimension :: remaining =>
//              val newYMultiplier = offsetYMultiplier * board.topology.maxDimensions(index)
//              val offsetPerPlane = currentYoffset + newYMultiplier + 1
//              dimensionOffset(
//                currentX,
//                currentY + coordInIndexDimension * offsetPerPlane,
//                remaining,
//                index + 1,
//                currentXoffset,
//                offsetPerPlane,
//                offsetXMultiplier,
//                newYMultiplier
//              )
//            case Nil => (currentX, currentY)
//          }
//        }
//
//        pos match {
//          case x :: y :: xs => dimensionOffset(x, y, xs)
//          case x :: Nil => (x, 0)
//          case _ => (0, 0)
//        }
//      }
//
//      for {
//        pos <- board.topology.indexes
//        state = if (board.isHidden(pos)) Box.Unclicked else Box.Clicked
//        number = if (board.isHidden(pos)) 0 else board.number(pos)
//        (x, y) = posToBoxPosition(pos)
//      } yield Box(x, y, state, number)
//
//
//    }


  }

}