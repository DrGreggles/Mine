package mine.board.topology

case class Torus(width: Int, height: Int) extends Topology[(Int, Int)] {

  override val indexes = {
    for {
      x <- 0 until width
      y <- 0 until height
    } yield (x, y)
  }.par.toSet

  override def surrounding(pos: (Int, Int)) = {
    val (centreX, centreY) = pos
    for {
      x <- centreX - 1 to centreX + 1
      y <- centreY - 1 to centreY + 1
      if centreX != x || centreY != y
    } yield (x mod width, y mod height)
  }.par.toSet

  override def adjacent(a: (Int, Int), b: (Int, Int)) = {
    val (ax, ay) = a
    val (bx, by) = b

    val xEdges = Set(0, width - 1)
    val yEdges = Set(0, height - 1)

    (math.abs(ay - by) <= 1 || ((yEdges contains ay) && (yEdges contains by))) &&
      (math.abs(ax - bx) <= 1 || ((xEdges contains ax) && (xEdges contains bx)))
  }

  override val size = width * height
  override val maxDimensions = Seq(width, height)
}