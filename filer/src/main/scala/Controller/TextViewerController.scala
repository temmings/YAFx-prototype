package Controller

import java.io.{BufferedInputStream, InputStream}
import java.nio.charset.Charset
import javafx.{concurrent => jfxc}

import Configuration.Default
import Model.FileItem.FileItem
import org.apache.commons.vfs2.{FileContent, FileObject}
import resource._

import scalafx.Includes._
import scalafx.concurrent._
import scalafx.scene.control.{Control, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class TextViewerController(viewer: TextArea) {
  private var sourceControl: Control = _
  private var currentItem: FileItem = _
  private var charset = Default.ViewerCharset
  private var isBinary = false
  private var isTextMode = true

  viewer.onKeyPressed = onKeyPressed
  viewer.onKeyReleased = onKeyReleased

  def open(source: Control, item: FileItem): Unit = {
    sourceControl = source
    currentItem = item

    viewer.setVisible(true)
    viewer.requestFocus

    detect(item.getContents)
    println(s"isBinary: $isBinary, charset: $charset")
    isTextMode = !isBinary
    show(currentItem.file)
  }

  private def detect(file: FileContent) = {
    for (in <- managed(file.getInputStream))
      isBinary = Util.Utils.isBinary(in)
    for (in <- managed(file.getInputStream)) {
      charset = Util.Utils.detectCharset(in)
        .map(Charset.forName)
        .getOrElse(Charset.defaultCharset())
    }
  }

  private def show(file: FileObject) = {
    val task =
      if (isTextMode)
        new ReadTextTask(file.getContent.getInputStream)
      else
        new ReadHexTask(file.getContent.getInputStream)
    viewer.text <== task.message
    // FIXME: タスク止める方法を提供する
    new Thread(task).start()
  }

  //noinspection ConvertExpressionToSAM
  class ReadTextTask(input: InputStream) extends Task(new jfxc.Task[Unit] {
    protected def call(): Unit = {
      updateMessage("Loading...")
      for (input <- managed(new BufferedInputStream(input))) {
        val byteStream = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
        updateMessage(new String(byteStream.toArray, charset))
      }
    }
  })

  //noinspection ConvertExpressionToSAM
  // FIXME: メモリ食いすぎなのでエコな実装にする
  class ReadHexTask(input: InputStream) extends Task(new jfxc.Task[Unit] {
    protected def call(): Unit = {
      updateMessage("Loading...")
      for (input <- managed(new BufferedInputStream(input))) {
        val s = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
        val sb = new StringBuilder()
        s.map(_.formatted("%02X "))
          .grouped(16)
          .map(x => x.foldRight("\n")(_ + _))
          .foreach(s => {
            sb.append(s)
            updateMessage(sb.toString)
          })
      }
    }
  })

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
            show(currentItem.file)
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
