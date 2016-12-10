package Controller

import java.io.File

import scala.io.Source
import scala.util.Properties
import scalafx.Includes._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class ViewerController(private val viewer: TextArea) {
  viewer.onKeyReleased = onKeyReleased
  var sourceControl: Control = null

  def open(sourceControl: Control, file: File) = {
    this.sourceControl = sourceControl
    val source = Source.fromFile(file, Configuration.App.ViewerDefaultCharset, Configuration.App.ViewerBufferSize)
    val lines = source.getLines
    val sb = new StringBuilder
    lines.foreach(l => sb.append(l + Properties.lineSeparator))
    source.close
    viewer.setText(sb.toString)
    viewer.setScrollTop(Double.MinValue)
    viewer.setVisible(true)
    viewer.requestFocus
  }

  def close = {
    viewer.setVisible(false)
    viewer.clear
    println(s"focus to ${sourceControl}")
    sourceControl.requestFocus
  }

  def onKeyReleased(e: KeyEvent) = {
    println(s"${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Enter => {
        e.consume
        close
      }
      case KeyCode.Escape => {
        e.consume
        close
      }
      case _ =>
    }
  }
}
