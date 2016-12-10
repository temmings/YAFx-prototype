import java.io.File

import scalafx.Includes._
import scalafx.scene.control.{ListView, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}

class ViewerController(val viewer: TextArea, val list: ListView[File]) {
  viewer.onKeyReleased = onKeyReleased

  def exitViewer = {
    viewer.setVisible(false)
    viewer.clear
    focusToList
  }

  def focusToList = {
    println(s"focus to ${list}")
    list.requestFocus
  }

  def onKeyReleased(e: KeyEvent) = {
    println(s"${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Enter => {
        e.consume
        exitViewer
      }
      case KeyCode.Escape => {
        e.consume
        exitViewer
      }
      case _ =>
    }
  }
}
