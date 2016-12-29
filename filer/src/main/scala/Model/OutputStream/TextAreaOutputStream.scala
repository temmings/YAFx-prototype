package Model.OutputStream

import java.io.{ByteArrayOutputStream, OutputStream}

import scala.io.Source
import scalafx.application.Platform
import scalafx.scene.control.TextArea

class TextAreaOutputStream(textArea: TextArea) extends OutputStream {
  private val buffer = new ByteArrayOutputStream

  override def write(i: Int): Unit = buffer.write(i)

  override def flush(): Unit = Platform.runLater {
    textArea.appendText(Source.fromBytes(buffer.toByteArray).mkString)
    buffer.reset()
  }
}
