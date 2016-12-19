package Controller

import java.io.File

import scalafx.Includes._
import scalafx.scene.control.Control
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.Pane

/**
  * Created by temmings on 12/19/16.
  */
class ImageViewController(container: Pane) {
  container.onKeyPressed = onKeyPressed
  container.onKeyReleased = onKeyReleased
  var sourceControl: Control = _

  def open(sourceControl: Control, file: File): Unit = {
    this.sourceControl = sourceControl
    container.setVisible(true)
    container.requestFocus
    val image = new Image(file.toURI.toString)
    val imageView = new ImageView(image) {
      preserveRatio = true
      fitHeight <== container.height
      fitWidth <== container.width
    }
    container.children.add(imageView)
  }

  private def close() = {
    container.setVisible(false)
    container.children.clear()
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
