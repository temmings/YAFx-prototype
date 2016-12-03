import java.io.File
import scala.sys.process.Process

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent, ScrollEvent}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class FileListController(val panel: AnchorPane,
                          val location: TextField,
                          val list: ListView[File]) {

  val fs = LocalFileSystem
  val editor = "C:/Users/temmings/Dropbox/Windows/vim74-kaoriya-win64/gvim.exe --remote-tab-silent"
  val defaultLocation = "C:/Users/temmings"
  var currentLocation = ""

  def initialize = {
    location.setMouseTransparent(true)
    list.setMouseTransparent(true)
    list.cellFactory = (_: ListView[File]) => new ListCell[File](new FileListCell())
    Platform.runLater(list.requestFocus)
    location.text.onChange { changeLocationHandler }
    setLocation(defaultLocation)
  }

  def changeLocationHandler = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(path)
    }
  }

  def editFile(file: File) = Process(s"${editor} ${file.getAbsolutePath}").run

  def setLocation(path: String) = {
    currentLocation = path
    location.setText(path)
    refreshFileList
    list.getSelectionModel.select(0)
  }
  def undoLocation = location.setText(currentLocation)

  def getCurrentItem = list.getSelectionModel.getSelectedItem
  def refreshFileList = list.items = ObservableBuffer(fs.getList(currentLocation))

  def focusToLocation = {
    println(s"focus to ${location}")
    location.requestFocus
  }
  def focusToList = {
    println(s"focus to ${list}")
    list.requestFocus
  }

  def onLocationKeyReleased(e: KeyEvent) = {
    println(s"${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Escape => {
        e.consume
        undoLocation
        focusToList
      }
      case _ =>
    }
  }

  def onListKeyPressed(e: KeyEvent) = {
    println(s"${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Enter => {
        e.consume
        val item = getCurrentItem
        if (item.isDirectory && item.canRead) {
          println(s"into directory: ${item.getCanonicalPath}")
          setLocation(item.getCanonicalPath)
        }
      }
      case KeyCode.BackSpace => {
        e.consume
        val current = new File(currentLocation)
        if (null != current) {
          if (null != current.getParent) {
            println(s"upto directory: ${current.getParent}")
            setLocation(current.getParent)
          }
        }
      }
      case KeyCode.Comma => e.consume
      case KeyCode.E => {
        e.consume
        val file = getCurrentItem
        if (!file.isDirectory) editFile(file)
      }
      case KeyCode.Q => {
        e.consume
        println(s"close ${FilerApp.stage}")
        FilerApp.stage.close()
      }
      case _ =>
    }
  }
  def onListKeyReleased(e: KeyEvent) = {
    e.code match {
      case KeyCode.Comma => {
        e.consume()
        focusToLocation
      }
      case _ =>
    }
  }

  initialize
}
