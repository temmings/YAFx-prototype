package Controller

import java.awt.Desktop
import java.io.File
import java.nio.file._

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}
import Control.FileListCell
import Model.{ListFile, LocationHistory}
import _root_.FileSystem.LocalFileSystem
import Utils.Utils

class FileListController(
                          location: TextField,
                          list: ListView[ListFile],
                          viewer: ViewerController,
                          imageViewer: ImageViewController) {
  private val fs = LocalFileSystem()
  private var pairFileList: FileListController = _
  var currentLocation: Path = _
  var histories: List[LocationHistory] = List[LocationHistory]()
  var showHiddenFiles: Boolean = false

  location.setMouseTransparent(true)
  location.text.onChange { changeLocationHandler() }
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[ListFile]) => fs match {
    case LocalFileSystem() => new ListCell[ListFile](new FileListCell())
  }
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(Paths.get(Configuration.App.DefaultLocation))

  private def listFileFilter(file: ListFile) = {
    if (showHiddenFiles) true
    else !file.isHidden
  }

  private def changeLocationHandler() = {
    val path = location.getText
    if (fs.canChangeDirectory(path)) {
      setLocation(Paths.get(path))
    }
  }

  def setPairFileListController(c: FileListController): Unit =
    pairFileList = c

  private def setLocation(path: Path): Unit = {
    val h = if (null != currentLocation) {
      if (currentLocation.normalize() == path.normalize()) return
      // TODO: 適切なリスト管理にする
      LocationHistory(currentLocation.normalize(), getCurrentItem)
    } else {
      LocationHistory(path.getParent, ListFile(path.toFile, path.toFile.getName))
    }
    println(f"add history: $h")
    histories +:= h

    currentLocation = path
    location.setText(currentLocation.toString)
    refresh()

    histories.find(_.location == currentLocation) match {
      case Some(LocationHistory(_, focus)) =>
        println(f"focus: $focus")
        list.getSelectionModel.select(focus)
        list.scrollTo(focus)
      case _ => list.getSelectionModel.selectFirst()
    }
  }
  private def undoLocation() = location.setText(currentLocation.toString)

  private def getCurrentItem = list.getSelectionModel.getSelectedItem
  private def refresh() = list.items = ObservableBuffer(fs.getList(currentLocation.toFile).filter(listFileFilter))

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
    Desktop.getDesktop.edit(file)
  }

  private def viewText(file: File) = {
    if (fs.canView(file)) {
      val isBinary = Utils.isBinaryFile(file)
      println(s"isBinaryFile: $isBinary")
      println(s"view file: ${file.getAbsolutePath}")
      if (!isBinary) viewer.open(list, file)
    }
  }

  private def viewImage(file: File) = {
    if (fs.canView(file)) {
      imageViewer.open(list, file)
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
    setLocation(file.toPath)
  }

  private def copyFile(file: ListFile) = {
    Files.copy(
      file.toFile.toPath,
      pairFileList.currentLocation.resolve(file.toFile.getName),
      StandardCopyOption.COPY_ATTRIBUTES)
    pairFileList.refresh()
  }

  private def toggleHiddenFiles() = {
    showHiddenFiles = !showHiddenFiles
    refresh()
  }

  private def actionOfContext(item: ListFile) = {
    (item.isDirectory, item.isImageFile) match {
      case (true, _) => cd(getCurrentItem.toFile)
      case (_, false) => viewText(getCurrentItem.toFile)
      case (_, true) => viewImage(getCurrentItem.toFile)
    }
  }

  private def onListKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      // defaults
      case KeyCode.Down | KeyCode.Up
           | KeyCode.PageDown | KeyCode.PageUp =>
        println(list.getSelectionModel.selectedItems)
        println(list.getFocusModel.getFocusedItem)
      case _ =>
        e.consume
        e.code match {
          case KeyCode.Tab => pairFileList.focusToList()
          case KeyCode.Enter => actionOfContext(getCurrentItem)
          case KeyCode.BackSpace => if (null != currentLocation.getParent) cd(currentLocation.getParent.toFile)
          case KeyCode.Period => toggleHiddenFiles()
          case KeyCode.C => copyFile(getCurrentItem)
          case KeyCode.E => editFile(getCurrentItem.toFile)
          case KeyCode.V => viewText(getCurrentItem.toFile)
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
