package Controller

import java.io.File
import java.nio.file.{Path}
import javafx.collections.ObservableList

import scala.sys.process.Process
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}
import Control.FileListCell
import FileSystem.LocalFileSystem
import Utils.Utils

case class FileListController(
                               location: TextField,
                               list: ListView[File],
                               viewer: ViewerController) {
  private val fs = LocalFileSystem()
  private var pairFileList: FileListController = null
  var currentLocation: Path = null

  location.setMouseTransparent(true)
  location.text.onChange { changeLocationHandler }
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[File]) => fs match {
    case LocalFileSystem() => new ListCell[File](new FileListCell())
  }
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(Configuration.App.defaultLocation)

  def changeLocationHandler = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(path)
    }
  }

  def setPairFileListController(c: FileListController) =
    pairFileList = c

  def setLocation(path: String) = {
    currentLocation = new File(path).toPath
    location.setText(path)
    refresh
    list.getSelectionModel.select(0)
  }
  def undoLocation = location.setText(currentLocation.toAbsolutePath.toString)

  def getCurrentItem = list.getSelectionModel.getSelectedItem
  def refresh = list.items = ObservableBuffer(fs.getList(currentLocation.toFile))

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

  def editFile(file: File) = {
    println(s"edit file: ${file.getAbsolutePath}")
    Process(s"${Configuration.App.editor} ${file.getAbsolutePath}").run
  }

  def viewFile(file: File) = {
    if (fs.canView(file)) {
      val isBinary = Utils.isBinaryFile(file)
      println(s"isBinaryFile: ${isBinary}")
      println(s"view file: ${file.getAbsolutePath}")
      if (!isBinary) viewer.open(list, file)
    }
  }

  def onLocationKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Escape => {
        e.consume
        undoLocation
        focusToList
      }
      case KeyCode.Tab => e.consume
      case _ =>
    }
  }

  def cd(file: File) = {
    println(s"change directory: ${file.getCanonicalPath}")
    setLocation(file.getCanonicalPath)
  }

  def copyFile(files: ObservableList[File]) = ???

  def onListKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      // defaults
      case KeyCode.Down | KeyCode.Up
           | KeyCode.PageDown | KeyCode.PageUp =>
      case _ => {
        e.consume
        e.code match {
          case KeyCode.Tab => pairFileList.focusToList
          case KeyCode.Enter =>
            if (getCurrentItem.isDirectory) cd(getCurrentItem)
            else viewFile(getCurrentItem)
          case KeyCode.BackSpace => cd(currentLocation.getParent.toFile)
          //case KeyCode.C => copyFile(list.getSelectionModel.getSelectedItems)
          case KeyCode.E => editFile(getCurrentItem)
          case KeyCode.V => viewFile(getCurrentItem)
          case KeyCode.Q => closeApp
          case _ =>

        }
      }
    }
  }
  def onListKeyReleased(e: KeyEvent) = {
    println(s"Released: ${e.code} on ${e.target} from ${e.source}")
    e.consume
    e.code match {
      case KeyCode.Comma => focusToLocation
      case _ =>
    }
  }
}
