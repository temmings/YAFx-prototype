package Controller

import java.io.BufferedInputStream
import java.nio.charset.Charset

import Model.FileItem
import org.apache.commons.vfs2.FileContent
import resource._

import scalafx.Includes._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class TextViewerController(viewer: TextArea) {
  viewer.onKeyPressed = onKeyPressed
  viewer.onKeyReleased = onKeyReleased
  var sourceControl: Control = _
  var currentItem: FileItem = _
  var charset: Charset = Charset.defaultCharset()
  var isBinary = false
  var isTextMode = true

  def open(source: Control, item: FileItem): Unit = {
    sourceControl = source
    currentItem = item

    viewer.setVisible(true)
    viewer.requestFocus

    detect(item.getContents)
    println(s"isBinary: $isBinary, charset: $charset")
    isTextMode = !isBinary
    show()
  }

  private def detect(file: FileContent) = {
    for (in <- managed(file.getInputStream))
      isBinary = Utils.Utils.isBinary(in)
    for (in <- managed(file.getInputStream)) {
      charset = Utils.Utils.detectCharset(in)
        .map(Charset.forName)
        .getOrElse(Charset.defaultCharset())
    }
  }

  private def show() = {
    if (isTextMode)
      showTextString()
    else
      showHexString()
  }

  private def showTextString() = {
    for (input <- managed(new BufferedInputStream(currentItem.getContents.getInputStream))) {
      val byteStream = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
      viewer.setText(new String(byteStream.toArray, charset))
    }
  }

  private def showHexString() = {
    for (input <- managed(new BufferedInputStream(currentItem.getContents.getInputStream))) {
      val s = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
      viewer.setText(
        s.map(_.formatted("%02X "))
          .grouped(16)
          .map(x => x.foldRight("\n")(_ + _))
          .mkString
      )
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
          case KeyCode.Tab =>
            isTextMode = !isTextMode
            show()
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
