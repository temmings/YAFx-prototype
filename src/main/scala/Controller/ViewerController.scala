package Controller

import java.io.BufferedInputStream

import Model.FileItem

import scala.io.Source
import scalafx.Includes._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class ViewerController(viewer: TextArea) {
  viewer.onKeyPressed = onKeyPressed
  viewer.onKeyReleased = onKeyReleased
  var sourceControl: Control = _

  def open(sourceControl: Control, item: FileItem): Unit = {
    this.sourceControl = sourceControl
    try {
      if (Utils.Utils.isBinary(item.getContents)) {
        val in = new BufferedInputStream(item.getContents)
        val body = Stream.continually(in.read).takeWhile(_ != -1)
          .map(_.toByte.formatted("%02X "))
          .grouped(16)
          .map(x => x.foldRight("\n")((n, z) => n + z))
          .mkString
        viewer.setText(body)
      } else {
        val body = Source.createBufferedSource(item.getContents).mkString
        viewer.setText(body)
      }
    } finally item.getContents.close()
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
