package Controller

import java.io.BufferedInputStream
import java.nio.charset.Charset

import Model.FileItem
import resource._

import scalafx.Includes._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class TextViewerController(viewer: TextArea) {
  viewer.onKeyPressed = onKeyPressed
  viewer.onKeyReleased = onKeyReleased
  var sourceControl: Control = _

  def open(sourceControl: Control, item: FileItem): Unit = {
    this.sourceControl = sourceControl
    val isBinary = {
      var flag = false
      for (in <- managed(item.getContents.getInputStream))
        flag = Utils.Utils.isBinary(in)
      flag
    }
    val encode = {
      var e: Charset = null
      for (in <- managed(item.getContents.getInputStream)) {
        e = Utils.Utils.detectCharset(in)
          .map(Charset.forName)
          .getOrElse(Charset.defaultCharset())
      }
      e
    }
    println(s"isBinary: $isBinary, encode: $encode")
    viewer.setScrollTop(Double.MinValue)
    viewer.setVisible(true)
    viewer.requestFocus
    for (input <- managed(new BufferedInputStream(item.getContents.getInputStream))) {
      val byteStream = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
      if (isBinary) {
        val body = byteStream
          .map(_.formatted("%02X "))
          .grouped(16)
          .map(x => x.foldRight("\n")(_ + _))
          .foreach(viewer.appendText)
      } else {
        viewer.appendText(new String(byteStream.toArray, encode))
      }
    }
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
