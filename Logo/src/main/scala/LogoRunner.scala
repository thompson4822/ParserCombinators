import swing._
import swing.event._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/19/11
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */

object LogoRunner extends SimpleSwingApplication {
  def top: Frame = {
    new MainFrame {
      title = "Celsius/Fahrenheit Converter"
      val label = new Label {
        text = "No button clicks registered"
      }

      val celsius = new TextField { columns = 5 }
      val fahrenheit = new TextField { columns = 5 }
      contents = new FlowPanel {
        contents += celsius
        contents += new Label(" Celsius = ")
        contents += fahrenheit
        contents += new Label(" Fahrenheit")
        border = Swing.EmptyBorder(15, 10, 10, 10)
      }
      listenTo(celsius, fahrenheit)
      reactions += {
        case EditDone(`celsius`) =>
          val c = celsius.text.toInt
          val f = c * 9 / 5 + 32
          fahrenheit.text = f.toString
        case EditDone(`fahrenheit`) =>
          val f = fahrenheit.text.toInt
          val c = (f - 32) * 5 / 9
          celsius.text = c.toString
      }
    }
  }
}