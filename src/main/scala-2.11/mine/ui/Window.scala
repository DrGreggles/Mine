package mine.ui

import java.awt.image.{BufferedImage, ImageObserver}
import java.awt.{Color, Dimension, Font}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.WindowConstants

import com.sun.javafx.iio.ImageStorage.ImageType
import mine.board.topology.Topology
import mine.viewer.Box.{Clicked, ClickedMine, Mine, Unclicked}
import mine.viewer.{Box, Viewer, WindowDimensions, WindowItem}

import scala.collection.GenSet
import scala.swing.event.MouseClicked
import scala.swing.{BoxPanel, Button, Color, MainFrame, Orientation, Panel, Swing, _}

class Window(solve: => Unit) extends MainFrame {

  peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  def topPanel = new BoxPanel(Orientation.Horizontal) {
    contents += Button("Solve")(solve)
    //TODO smiley face
    //TODO mines remaining number
    //TODO time
  }

  def render(items: GenSet[WindowItem]) = {
    paint(items.toList.toSet)
    repaint
  }

  var image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)

  def setMainPanelSize(width: Int, height: Int) = {
    val borderWidth = size.width - mainPanel.size.width
    val borderHeight = size.height - mainPanel.size.height
    size = new Dimension(width + borderWidth, height + borderHeight)
  }

  def paint(items: GenSet[WindowItem]) = {
    val g = image.getGraphics
    g setFont numFont
    items foreach {
      case Box(x, y, Unclicked, _) =>
        g.drawImage(unclickedBox, x * 16, y * 16, Color.BLACK, null)
      case Box(x, y, Clicked, number) =>
        g setColor numColour(number)
        g.drawImage(clickedBox, x * 16, y * 16, Color.BLACK, null)

        if (number > 0) g.drawString(number.toString, x * 16 + 4, y * 16 + 12)
      case Box(x, y, Mine, _) =>
        g.drawImage(unclickedBox, x * 16, y * 16, Color.BLACK, null)
      case Box(x, y, ClickedMine, _) =>
        g.drawImage(unclickedBox, x * 16, y * 16, Color.BLACK, null)
      case WindowDimensions(x, y) =>
        setMainPanelSize(x * 16, y * 16)
    }
  }

  val unclickedBox: BufferedImage = ImageIO.read(new File(getClass.getResource("/unclicked.png").getPath))
  val clickedBox: BufferedImage = ImageIO.read(new File(getClass.getResource("/clicked.png").getPath))

  def onPaint(g: Graphics2D) {
    //    g setFont numFont
    //    items foreach {
    //      case Box(x, y, Unclicked, _) =>
    //        g.drawImage(unclickedBox, null, x * 16, y * 16)
    //      case Box(x, y, Clicked, number) =>
    //        g setColor numColour(number)
    //        g.drawImage(clickedBox, null, x * 16, y * 16)
    //
    //        if (number > 0) g.drawString(number.toString, x * 16+4, y * 16+12)
    //      case Box(x, y, Mine, _) => g.drawImage(unclickedBox, null, x * 16, y * 16)
    //      case Box(x, y, ClickedMine, _) => g.drawImage(unclickedBox, null, x * 16, y * 16)
    //    }
    g.drawImage(image, 0, 0, null)
  }


  val backgroundColour = new Color(0, 0, 0)
  val colour0 = new Color(0, 0, 128)
  val colour1 = new Color(0, 0, 255)
  val colour2 = new Color(0, 128, 0)
  val colour3 = new Color(255, 0, 0)
  val colour4 = new Color(0, 0, 128)
  val colour5 = new Color(128, 0, 0)
  val colour6 = new Color(0, 128, 128)
  val colour7 = new Color(128, 0, 128)
  val colour8 = new Color(70, 70, 70)
  val colour9 = new Color(255, 0, 255)

  private def numColour(number: Int) = number % 10 match {
    case 0 => colour0
    case 1 => colour1
    case 2 => colour2
    case 3 => colour3
    case 4 => colour4
    case 5 => colour5
    case 6 => colour6
    case 7 => colour7
    case 8 => colour8
    case 9 => colour9
    case _ => colour0
  }


  val numFont = new Font("Fixedsys", Font.BOLD, 16)

  val mainPanel = new Panel {

    focusable = true
    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(_, point, _, _, _) =>
        onClick(point)
        repaint
    }

    override def paint(g: Graphics2D) {
      g setColor backgroundColour
      g fillRect(0, 0, size.width, size.height)
      onPaint(g)
    }

  }

  def onClick(point: Point) = {

  }

  title = "Topology Sweeper"
  preferredSize = new Dimension(520, 420)
  contents = new BoxPanel(Orientation.Vertical) {
    contents += topPanel
    contents += mainPanel
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }
}