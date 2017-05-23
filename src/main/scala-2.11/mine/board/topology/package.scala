package mine.board

package object topology {

  implicit class IntOps(x: Int) {
    def mod(y: Int) = if (x > 0) x % y else (x + y) % y
  }

}
