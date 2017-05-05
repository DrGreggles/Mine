package mine

import mine.domain._
import mine.viewer.Viewer

import scala.annotation.tailrec
import scala.collection.parallel.ParSet
import scala.collection.{GenSeq, GenSet}
import scala.util.Random

object Main extends App with Solver {


  var wins = 0
  var losses = 0

  def newBoard = Game.expert

  type Pos = (Int, Int)

  def play(n: Int): Unit = (1 to n) foreach { i =>
    println(s"Game: $i")
    solve(newBoard, ParSet.empty)
  }

  play(20)
  println(s"WINS: $wins, LOSSES: $losses")
}
