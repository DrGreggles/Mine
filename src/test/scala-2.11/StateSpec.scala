import mine.domain
import mine.domain.{BoardState, Grid, Game$}
import org.scalatest._

class StateSpec extends FlatSpec with Matchers {

  "focus" should "be invariant of flips and rotations" in {

    val mask = Grid(100, 100, {
      case (x,y) => x>5 && y>5 && x<70 && y<70
    })

    val mines = mask

    val board = Game(mask, mines)

    val state = BoardState(board)

    state.focus(4,20) == state.focus(71, 20)
    state.focus(4,20) == state.focus(20, 71)
    state.focus(4,20) == state.focus(20, 4)
  }

}