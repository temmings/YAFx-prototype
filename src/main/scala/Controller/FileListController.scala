package Controller

import java.io.File
import java.nio.file.Path

import scala.sys.process.Process
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}
import Control.{FileListCell, ListFile}
import FileSystem.LocalFileSystem
import Utils.Utils

case class FileListController(
                               location: TextField,
                               list: ListView[ListFile],
                               viewer: ViewerController) {
  private val fs = LocalFileSystem()
  private var pairFileList: FileListController = _
  var currentLocation: Path = _

  location.setMouseTransparent(true)
  location.text.onChange { changeLocationHandler() }
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[ListFile]) => fs match {
    case LocalFileSystem() => new ListCell[ListFile](new FileListCell())
  }
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(Configuration.App.DefaultLocation)

  private def changeLocationHandler() = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(path)
    }
  }

  def setPairFileListController(c: FileListController): Unit =
    pairFileList = c

  private def setLocation(path: String) = {
    currentLocation = new File(path).toPath
    location.setText(path)
    refresh()
    list.getSelectionModel.select(0)
  }
  private def undoLocation() = location.setText(currentLocation.toAbsolutePath.toString)

  private def getCurrentItem = list.getSelectionModel.getSelectedItem
  private def refresh() = list.items = ObservableBuffer(fs.getList(currentLocation.toFile))

  private def focusToLocation() = {
    println(s"focus to $location")
    location.requestFocus
  }
  private def focusToList() = {
    println(s"focus to $list")
    list.requestFocus
  }

  private def closeApp() = {
    Platform.exit
  }

  private def editFile(file: File) = {
    println(s"edit file: ${file.getAbsolutePath}")
    Process(s"${Configuration.App.Editor} ${file.getAbsolutePath}").run
  }

  private def viewFile(file: File) = {
    if (fs.canView(file)) {
      val isBinary = Utils.isBinaryFile(file)
      println(s"isBinaryFile: $isBinary")
      println(s"view file: ${file.getAbsolutePath}")
      if (!isBinary) viewer.open(list, file)
    }
  }

  private def onLocationKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Escape =>
        e.consume
        undoLocation()
        focusToList()
      case KeyCode.Tab => e.consume
      case _ =>
    }
  }

  private def cd(file: File) = {
    println(s"change directory: ${file.getCanonicalPath}")
    setLocation(file.getCanonicalPath)
  }

  //private def copyFile(files: ObservableList[File]) = ???

  private def onListKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      // defaults
      case KeyCode.Down | KeyCode.Up
           | KeyCode.PageDown | KeyCode.PageUp =>
      case _ =>
        e.consume
        e.code match {
          case KeyCode.Tab => pairFileList.focusToList()
          case KeyCode.Enter =>
            if (getCurrentItem.toFile.isDirectory) cd(getCurrentItem.toFile)
            else viewFile(getCurrentItem.toFile)
          case KeyCode.BackSpace => cd(currentLocation.getParent.toFile)
          //case KeyCode.C => copyFile(list.getSelectionModel.getSelectedItems)
          case KeyCode.E => editFile(getCurrentItem.toFile)
          case KeyCode.V => viewFile(getCurrentItem.toFile)
          case KeyCode.Q => closeApp()
          case _ =>

        }
    }
  }
  private def onListKeyReleased(e: KeyEvent) = {
    println(s"Released: ${e.code} on ${e.target} from ${e.source}")
    e.consume
    e.code match {
      case KeyCode.Comma => focusToLocation()
      case _ =>
    }
  }
}
