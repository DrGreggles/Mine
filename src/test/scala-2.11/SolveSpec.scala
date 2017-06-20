import mine.board.Board
import mine.board.topology._
import org.scalacheck._
import org.scalacheck.Gen._
import org.scalacheck.Prop.forAll

import scala.util.Random

object SolveSpec extends Properties("Solver") with BoardGen {

  property("Solver never gives a mine back as a non-mine") = forAll { (board: AnyBoard) =>
    val positions = board.topology.indexes
    val n = Random.nextInt(positions.size)

    val randomPos = positions.iterator.drop(n).next

    board.click(randomPos)
  }

  property("Solver never gives a non-mine back as a mine") = ???

}

trait BoardGen {

  type AnyBoard = Board[(Topology[_])#P, Topology[_]]

  implicit val squaresGen = for {
    x <- Gen.chooseNum(1, 10)
    y <- Gen.chooseNum(1, 10)
  } yield Squares(x, y)

  implicit val cylinderGen = for {
    x <- Gen.chooseNum(1, 10)
    y <- Gen.chooseNum(1, 10)
  } yield Cylinder(x, y)

  implicit val torusGen = for {
    x <- Gen.chooseNum(1, 10)
    y <- Gen.chooseNum(1, 10)
  } yield Torus(x, y)

  implicit val hypercubeGen = for {
    dimension <- Gen.chooseNum(1, 5)
    dimensions <- Gen.listOfN(dimension, Gen.chooseNum(1, 5))
  } yield Hypercube(dimensions)


  val topologyGen: Gen[Topology[_]] = oneOf(squaresGen, cylinderGen, torusGen, hypercubeGen)

  val boardGen = for {
    topology <- topologyGen
    mines <- Gen.chooseNum(1,20)
  } yield Board(topology, mines)

  implicit val arbBoard = Arbitrary(boardGen)
}