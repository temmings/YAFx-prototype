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
  val defaultLocation = "C:/"

  def initialize = {
    list.cellFactory = (_: ListView[File]) => new ListCell[File](new FileListCell())
    location.text.onChange {
      refreshFileList
    }
    setLocation(defaultLocation)
  }

  def onPanelKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on Panel")
    if (e.code == KeyCode.Escape) {
      println("close stage")
      FilerApp.stage.close()
    }
  }

  def onLocationKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on Location")
  }

  def onListKeyPressed(e: KeyEvent) = {
    println(s"press ${e.code} on List")
  }

  def setLocation(path: String) = {
    location.setText(path)
  }

  def refreshFileList = {
    if (fs.isDirectory(location.getText)) {
      list.items = ObservableBuffer(fs.getList(location.getText))
    }
  }

  initialize
}
