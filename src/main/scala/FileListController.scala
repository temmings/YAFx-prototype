import java.io.File

import scala.io.Source
import scala.sys.process.Process
import scala.util.Properties
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent, ScrollEvent}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class FileListController(
                          val panel: AnchorPane,
                          val location: TextField,
                          val list: ListView[File],
                          val viewer: TextArea) {

  val fs = LocalFileSystem
  var currentLocation = ""

  def initialize = {
    location.setMouseTransparent(true)
    list.setMouseTransparent(true)
    list.cellFactory = (_: ListView[File]) => new ListCell[File](new FileListCell())
    Platform.runLater(list.requestFocus)
    location.text.onChange { changeLocationHandler }
    setLocation(Configuration.defaultLocation)
  }

  def changeLocationHandler = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(path)
    }
  }

  def editFile(file: File) = Process(s"${Configuration.editor} ${file.getAbsolutePath}").run

  def viewFile(file: File) = {
    val source = Source.fromFile(file, Configuration.ViewerDefaultCharset, Configuration.ViewerBufferSize)
    val lines = source.getLines
    val sb = new StringBuilder
    lines.foreach(l => sb.append(l + Properties.lineSeparator))
    source.close
    viewer.setText(sb.toString)
    viewer.setScrollTop(Double.MinValue)
    viewer.setVisible(true)
    viewer.requestFocus
  }
  def exitViewer = {
    viewer.setVisible(false)
    viewer.clear
    focusToList
  }

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

  def onListKeyReleased(e: KeyEvent) = {
    def _viewFile(file: File) = {
      if (fs.canView(file)) {
        val isJunctionOrSymlink = NativeUtils.isJunctionOrSymlink(file)
        println(s"isJunctionOrSymlink: ${isJunctionOrSymlink}")
        val isBinary = Utils.isBinaryFile(file)
        println(s"isBinaryFile: ${isBinary}")
        println(s"view file: ${file.getAbsolutePath}")
        if (!isBinary) viewFile(file)
      }
    }
    e.code match {
      case KeyCode.Enter => {
        e.consume
        val file = getCurrentItem
        if (file.isDirectory && file.canRead) {
          println(s"into directory: ${file.getCanonicalPath}")
          setLocation(file.getCanonicalPath)
        } else _viewFile(file)
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
      case KeyCode.Comma => {
        e.consume
        focusToLocation
      }
      case KeyCode.E => {
        e.consume
        val file = getCurrentItem
        if (fs.canChangeDirectory(file)) {
          println(s"edit file: ${file.getAbsolutePath}")
          editFile(file)
        }
      }
      case KeyCode.V => {
        e.consume
        _viewFile(getCurrentItem)
      }
      case KeyCode.Q => {
        e.consume
        println(s"close ${FilerApp.stage}")
        FilerApp.stage.close()
      }
      case _ =>
    }
  }

  def onViewerKeyReleased(e: KeyEvent) = {
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

  initialize
}
