package Controller

import java.io.File

import scala.io.Source
import scala.util.Properties
import scalafx.Includes._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

case class ViewerController(viewer: TextArea) {
  viewer.onKeyPressed = onKeyPressed
  viewer.onKeyReleased = onKeyReleased
  var sourceControl: Control = _

  def open(sourceControl: Control, file: File): Unit = {
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

  private def close() = {
    viewer.setVisible(false)
    viewer.clear()
    println(s"focus to $sourceControl")
    sourceControl.requestFocus
  }

  private def onKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      // defaults
      case KeyCode.Down | KeyCode.Up | KeyCode.Space
           | KeyCode.PageDown | KeyCode.PageUp =>
      case _ =>
        e.consume
        e.code match {
          case KeyCode.Enter => close()
          case KeyCode.Escape => close()
          case _ =>
        }
    }
  }
  private def onKeyReleased(e: KeyEvent) = {
    println(s"Released: ${e.code} on ${e.target} from ${e.source}")
    e.consume
  }
}
