package mine.board.topology

case class Squares(width: Int, height: Int) extends Topology[(Int, Int)] {

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
      if x >= 0
      if x < width
      y <- centreY - 1 to centreY + 1
      if y >= 0
      if y < height
      if centreX != x || centreY != y
    } yield (x, y)
  }.par.toSet

  override def adjacent(a: (Int, Int), b: (Int, Int)) = {
    val (ax, ay) = a
    val (bx, by) = b

    math.abs(ax - bx) <= 1 && math.abs(ay - by) <= 1
  }

  override val size = width * height
  override val maxDimensions = Seq(width, height)
}
