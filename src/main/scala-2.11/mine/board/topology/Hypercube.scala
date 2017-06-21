package mine.board.topology


case class Hypercube(dimensions: Int*) extends Topology {

  type Pos = List[Int]

  override val indexes = (dimensions foldLeft Set(List.empty[Int])) { (accum, dimension) =>
    accum flatMap { vector =>
      (0 until dimension) map (vector :+ _)
    }
  }.par

  override def surrounding(pos: Pos) = {

    val coordRanges = pos.zipWithIndex map { case (x, i) =>
      Math.max(x - 1, 0) to Math.min(x + 1, dimensions(i) - 1)
    }

    val adjacentHypercube = (coordRanges foldLeft Set(List.empty[Int])) { (accum, range) =>
      accum flatMap { initCoords =>
        range map (initCoords :+ _)
      }
    }

    (adjacentHypercube - pos).par
  }

  private def adjacentPair(pair: (Int, Int)) = {
    val (a, b) = pair
    math.abs(a - b) <= 1
  }

  override def adjacent(a: Pos, b: Pos) = {
    ((a zip b) foldLeft true) (_ && adjacentPair(_))
  }

  override val size = (dimensions foldLeft 1) (_ * _)

  override def maxDimensions: Seq[Int] = dimensions
}