package mine.board

import mine.board.topology.{Cylinder, Hypercube, Squares, Torus}
import mine.viewer.Renderer

class BoardFactory(implicit renderer: Renderer) {

  def beginner = Board[(Int, Int), Squares](Squares(8, 8), mines = 10)

  def intermediate = Board[(Int, Int), Squares](Squares(16, 16), mines = 40)

  def expert = Board[(Int, Int), Squares](Squares(30, 16), mines = 99)

  def expertCylinder = Board[(Int, Int), Cylinder](Cylinder(30, 16), mines = 99)

  def expertTorus = Board[(Int, Int), Torus](Torus(30, 16), mines = 99)

  def smallCube = Board[List[Int], Hypercube](Hypercube(6, 6, 6), mines = 30)

  def smallTesseract = Board[List[Int], Hypercube](Hypercube(4, 4, 4, 4), mines = 10)

  def small5Cube = Board[List[Int], Hypercube](Hypercube(3, 3, 3, 3, 3), mines = 7)
}