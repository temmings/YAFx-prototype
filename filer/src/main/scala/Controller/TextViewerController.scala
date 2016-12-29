package Controller

import java.io.BufferedInputStream
import java.nio.charset.Charset
import javafx.{concurrent => jfxc}

import Configuration.Default
import Model.FileItem.FileItem
import org.apache.commons.vfs2.FileContent
import resource._

import scala.io.{Codec, Source}
import scala.util.control.Breaks
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
  private val readTextService = new ReadTextService()
  private val readHexService = new ReadHexService()

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
    show()
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

  private def show() = {
    val service =
      if (isTextMode)
        readTextService
      else
        readHexService
    service.restart()
    viewer.text <== service.value
  }

  class ReadTextService extends Service(new jfxc.Service[String] {
    //noinspection ConvertExpressionToSAM
    protected def createTask(): jfxc.Task[String] = new jfxc.Task[String] {
      val sb = new StringBuilder()
      val b = new Breaks

      protected def call(): String = {
        updateTitle("read text")
        updateMessage("Loading...")
        sb.clear()
        val source = Source.fromInputStream(
          currentItem.file.getContent.getInputStream)(Codec.charset2codec(charset))
        b.breakable {
          for (line <- source.getLines) {
            if (isCancelled) b.break()
            sb.append(f"$line\n")
            updateValue(sb.toString)
          }
        }
        updateMessage("Done.")
        sb.toString()
      }
    }
  })

  class ReadHexService extends Service(new jfxc.Service[String] {
    //noinspection ConvertExpressionToSAM
    protected def createTask(): jfxc.Task[String] = new jfxc.Task[String] {
      val sb = new StringBuilder()
      val b = new Breaks

      protected def call(): String = {
        updateTitle("read hex")
        updateMessage("Loading...")
        sb.clear()
        for (input <- managed(new BufferedInputStream(currentItem.file.getContent.getInputStream))) {
          val s = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte)
          b.breakable {
            for (g <- s.grouped(16)) {
              if (isCancelled) b.break()
              sb.append(g.map(_.formatted("%02X")).mkString(" "))
              sb.append("\n")
              updateValue(sb.toString)
            }
          }
        }
        updateMessage("Done.")
        sb.toString()
      }
    }
  })

  private def close() = {
    if (isTextMode)
      readTextService.cancel()
    else
      readHexService.cancel()
    viewer.setVisible(false)
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
            if (isTextMode)
              readTextService.cancel()
            else
              readHexService.cancel()
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
