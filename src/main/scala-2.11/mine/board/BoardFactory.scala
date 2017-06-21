package mine.board

import mine.board.topology.{Cylinder, Hypercube, Squares, Torus}
import mine.viewer.Renderer

class BoardFactory(implicit renderer: Renderer) {

  def beginner = Board(Squares(8, 8), mines = 10)

  def intermediate = Board(Squares(16, 16), mines = 40)

  def expert = Board(Squares(30, 16), mines = 99)

  def expertCylinder = Board(Cylinder(30, 16), mines = 99)

  def expertTorus = Board(Torus(30, 16), mines = 99)

  def smallCube = Board(Hypercube(6, 6, 6), mines = 30)

  def smallTesseract = Board(Hypercube(4, 4, 4, 4), mines = 10)

  def small5Cube = Board(Hypercube(3, 3, 3, 3, 3), mines = 7)
}