package Controller

import java.io.{OutputStream, PrintStream}

import Configuration.Default

import scala.io.Source
import scalafx.application.Platform
import scalafx.scene.control.TextArea

class ConsoleController(textArea: TextArea) {
  val console = new Console
  val ps = new PrintStream(console, true)
  System.setOut(ps)
  System.setErr(ps)
  System.err.flush()
  System.out.flush()

  protected class Console extends OutputStream {
    override def write(b: Array[Byte]): Unit = {
      Platform.runLater {
        textArea.appendText(Source.fromBytes(b)(Default.ViewerCharset).mkString)
      }
    }

    override def write(b: Int): Unit = write(BigInt(b).toByteArray)
  }

}
