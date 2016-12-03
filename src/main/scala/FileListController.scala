import java.io.File

import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class FileListController(val panel: AnchorPane,
                          val location: TextField,
                          val list: ListView[File]) {

  val fs = LocalFileSystem
  val defaultLocation = "C:/Users/temmings"

  def initialize = {
    list.cellFactory = (_: ListView[File]) => new ListCell[File](new FileListCell())
    location.text.onChange {
      refreshFileList
    }
    setLocation(defaultLocation)
  }
  def getLocation = location.getText
  def setLocation(path: String) = location.setText(path)
  def getCurrentItem = list.getSelectionModel.getSelectedItem

  def refreshFileList = {
    if (fs.isDirectory(getLocation)) {
      list.items = ObservableBuffer(fs.getList(getLocation))
    }
  }

  def onPanelKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on Panel")
    e.code match {
      case _ =>
    }
  }

  def onLocationKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on Location")
    e.code match {
      case _ =>
    }
  }

  def onListKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on List")
    e.code match {
      case KeyCode.Enter => {
        if (getCurrentItem.isDirectory)
          setLocation(getCurrentItem.getCanonicalPath)
      }
      case KeyCode.BackSpace => {
        val current = new File(getLocation)
        if (null != current) {
          if (null != current.getParent) {
            setLocation(current.getParent)
          }
        }
      }
      case KeyCode.Q => {
        FilerApp.stage.close()
        println("close stage")
      }
      case _ => {
        // FIXME: delegate KeyEvent to Panel
        println(s"delegate KeyEvent to Panel: ${e.code}")
        onPanelKeyPressed(e)
      }
    }
  }

  initialize
}
