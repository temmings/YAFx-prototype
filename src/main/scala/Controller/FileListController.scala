package Controller

import java.awt.Desktop
import java.nio.file._

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}
import Control.FileListCell
import Model.{ListFile, LocationHistory}
import _root_.FileSystem.{LocalFileSystem, ZipFileSystem}

class FileListController(
                          location: TextField,
                          list: ListView[ListFile],
                          viewer: ViewerController,
                          imageViewer: ImageViewController) {
  private var pairFileList: FileListController = _
  var currentLocation: Path = _
  var histories: List[LocationHistory] = List[LocationHistory]()
  var showHiddenFiles: Boolean = false

  location.setMouseTransparent(true)
  //location.text.onChange { changeLocationHandler() }
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[ListFile]) => new ListCell[ListFile](new FileListCell())
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(Paths.get(Configuration.App.DefaultLocation))

  private def changeLocationHandler() = {
    val path = location.getText
    setLocation(Paths.get(path))
  }

  def setPairFileListController(c: FileListController): Unit =
    pairFileList = c

  private def setLocation(path: Path): Unit = {
    if (currentLocation == path) return
    val history = Option(currentLocation) match {
      case None =>
        LocationHistory(path.getParent, ListFile.fromPath(detectFileSystem(path)(path), path))
      case Some(current) =>
        LocationHistory(current.normalize(), getCurrentItem)
    }
    // TODO: 適切なリスト管理にする
    histories = (history :: histories.filterNot(history.==)).take(Configuration.App.MaxDirHistories)
    println(f"add history: $history")

    currentLocation = path.normalize()
    location.setText(path.normalize().toString)
    refresh()

    histories.find(_.location == currentLocation) match {
      case Some(LocationHistory(_, focus)) =>
        println(f"focus: $focus")
        list.getSelectionModel.select(focus)
        list.scrollTo(focus)
      case _ =>
        list.getSelectionModel.selectFirst()
        list.scrollTo(0)
    }
  }
  private def undoLocation() = location.setText(currentLocation.toString)

  private def detectFileSystem(path: Path) = {
    val filename = Option(path.getFileName).getOrElse("").toString
    filename.toLowerCase.split('.').last match {
      case "zip" => ZipFileSystem
      case _ => LocalFileSystem
    }
  }

  private def getCurrentItem = list.getSelectionModel.getSelectedItem
  private def refresh() = {
    val fs = detectFileSystem(currentLocation)
    def filter(file: ListFile) = showHiddenFiles || !file.isHidden
    val items = fs(currentLocation).listFiles().filter(filter)
    list.items = ObservableBuffer(items)
  }

  private def focusToLocation() = {
    println(s"focus to $location")
    location.requestFocus
  }
  private def focusToList() = {
    println(s"focus to $list")
    list.requestFocus
  }

  private def closeApp() = {
    Platform.exit()
    System.exit(0)
  }

  private def editFile(item: ListFile) = {
    println(s"editFile($item)")
    Desktop.getDesktop.edit(item.toFile)
  }

  private def viewText(item: ListFile) = {
    println(s"viewText:($item)")
    viewer.open(list, item)
  }

  private def viewImage(item: ListFile) = {
    imageViewer.open(list, item)
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

  private def cd(path: Path) = {
    println(f"cd:($path)")
    setLocation(path)
  }

  private def copyFile(file: ListFile) = {
    Files.copy(
      file.path,
      pairFileList.currentLocation.resolve(file.toFile.getName),
      StandardCopyOption.COPY_ATTRIBUTES)
    pairFileList.refresh()
  }

  private def toggleHiddenFiles() = {
    showHiddenFiles = !showHiddenFiles
    refresh()
  }

  private def actionOfContext(item: ListFile) = {
    (item.isDirectory, item.isArchive, item.isImageFile) match {
      case (true, _, _) => cd(getCurrentItem.path)
      case (_, true, _) => cd(getCurrentItem.path)
      case (_, _, false) => viewText(getCurrentItem)
      case (_, _, true) => viewImage(getCurrentItem)
    }
  }

  private def onListKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      // defaults
      case KeyCode.Down | KeyCode.Up
           | KeyCode.PageDown | KeyCode.PageUp =>
      case _ =>
        e.consume
        (e.shiftDown, e.code) match {
          case (false, KeyCode.Tab) => pairFileList.focusToList()
          case (false, KeyCode.Enter) => actionOfContext(getCurrentItem)
          case (false, KeyCode.BackSpace) => Option(currentLocation.getParent).foreach(p => cd(p))
          case (false, KeyCode.Period) => toggleHiddenFiles()
          case (false, KeyCode.BackSlash) => cd(currentLocation.getRoot)
          case (false, KeyCode.C) => copyFile(getCurrentItem)
          case (false, KeyCode.E) => editFile(getCurrentItem)
          case (true,  KeyCode.U) => if (getCurrentItem.isArchive) cd(getCurrentItem.path)
          case (false, KeyCode.V) => viewText(getCurrentItem)
          case (false, KeyCode.Q) => closeApp()
          case _ =>

        }
    }
  }
  private def onListKeyReleased(e: KeyEvent) = {
    println(s"Released: ${e.code} on ${e.target} from ${e.source}")
    e.consume
    e.code match {
      //case KeyCode.Comma => focusToLocation()
      case _ =>
    }
  }
}
