package Controller

import java.io.File

import scala.sys.process.Process
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}

import Control.FileListCell
import FileSystem.{LocalFileSystem}
import Utils.{Utils, NativeUtils}

class FileListController(
                          private val location: TextField,
                          private val list: ListView[File],
                          private val viewer: ViewerController) {
  private val fs = new LocalFileSystem
  var currentLocation = ""

  location.setMouseTransparent(true)
  location.text.onChange { changeLocationHandler }
  location.onKeyReleased = onLocationKeyReleased

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[File]) => fs match {
    case _: LocalFileSystem => new ListCell[File](new FileListCell())
  }
  list.onKeyReleased = onListKeyReleased

  setLocation(Configuration.App.defaultLocation)

  def changeLocationHandler = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(path)
    }
  }

  def setLocation(path: String) = {
    currentLocation = path
    location.setText(path)
    refresh
    list.getSelectionModel.select(0)
  }
  def undoLocation = location.setText(currentLocation)

  def getCurrentItem = list.getSelectionModel.getSelectedItem
  def refresh = list.items = ObservableBuffer(fs.getList(currentLocation))

  def focusToLocation = {
    println(s"focus to ${location}")
    location.requestFocus
  }
  def focusToList = {
    println(s"focus to ${list}")
    list.requestFocus
  }

  def closeApp = {
    Platform.exit
  }

  def editFile(file: File) = Process(s"${Configuration.App.editor} ${file.getAbsolutePath}").run

  def viewFile(file: File) = {
    viewer.open(list, file)
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
        closeApp
      }
      case _ =>
    }
  }
}
