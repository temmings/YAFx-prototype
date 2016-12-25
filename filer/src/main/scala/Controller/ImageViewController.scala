package Controller

import Model.FileItem.FileItem
import resource._

import scalafx.Includes._
import scalafx.scene.control.Control
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.Pane

class ImageViewController(container: Pane) {
  private var sourceControl: Control = _

  container.onKeyPressed = onKeyPressed
  container.onKeyReleased = onKeyReleased

  def open(sourceControl: Control, item: FileItem): Unit = {
    this.sourceControl = sourceControl
    container.setVisible(true)
    container.requestFocus
    for (input <- managed(item.getContents.getInputStream)) {
      val image = new Image(input)
      val imageView = new ImageView(image) {
        preserveRatio = true
        fitHeight <== container.height
        fitWidth <== container.width
      }
      container.children.add(imageView)
    }
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
