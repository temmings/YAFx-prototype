package Controller

import java.awt.Desktop
import java.nio.file._

import Control.ItemCell
import Model._
import _root_.FileSystem.{LocalFileSystem, ZipFileSystem}

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}

class FileListController(
                          location: TextField,
                          list: ListView[FormatItem],
                          viewer: ViewerController,
                          imageViewer: ImageViewController) {
  private var pairFileList: FileListController = _
  var histories: List[LocationHistory] = List[LocationHistory]()
  var showHiddenFiles: Boolean = false
  var currentLocation: Item = Configuration.App.DefaultLocation

  location.setMouseTransparent(true)
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[FormatItem]) => new ListCell[FormatItem](new ItemCell())
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(currentLocation)

  def setPairFileListController(c: FileListController): Unit = pairFileList = c

  private def appendHistory(item: FormatItem) = {
    val history = LocationHistory(currentLocation.id, item)
    // TODO: 適切なリスト管理にする
    histories = (history :: histories.filterNot(history.==)).take(Configuration.App.MaxDirHistories)
    println(f"add history: $history")
  }

  private def getHistory(item: Item) = {
    histories.find(_.location == item.id)
  }

  private def setLocation(item: Item): Unit = {
    getCurrentItem.foreach(appendHistory)

    currentLocation = item
    location.setText(item.id)
    refresh()

    getHistory(currentLocation) match {
      case Some(LocationHistory(_, focus)) =>
        println(f"focus: $focus")
        list.getSelectionModel.select(focus)
        list.scrollTo(focus)
      case _ =>
        list.getSelectionModel.selectFirst()
        list.scrollTo(0)
    }
  }
  private def undoLocation() = location.setText(currentLocation.id)

  private def detectFileSystem(item: Item) = {
    val filename = Option(Paths.get(item.id).getFileName).getOrElse("").toString
    filename.toLowerCase.split('.').last match {
      case "zip" => ZipFileSystem
      case _ => LocalFileSystem
    }
  }

  private def getCurrentItem = Option(list.getSelectionModel.getSelectedItem)
  private def refresh() = {
    val fs = detectFileSystem(currentLocation)
    def filter(item: FileItem) = showHiddenFiles || !item.isHidden
    val items = fs(Paths.get(currentLocation.id)).listFiles().filter(filter)
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

  private def editFile(item: FileItem) = {
    println(s"editFile($item)")
    Desktop.getDesktop.edit(item.toFile)
  }

  private def viewText(item: FileItem) = {
    println(s"viewText:($item)")
    viewer.open(list, item)
  }

  private def viewImage(item: FileItem) = {
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

  private def cd(item: Item) = {
    println(f"cd:($item)")
    setLocation(item)
  }

  private def copyFile(item: FileItem) = {
    Files.copy(
      item.toPath,
      Paths.get(pairFileList.currentLocation.id).resolve(item.toFile.getName),
      StandardCopyOption.COPY_ATTRIBUTES)
    pairFileList.refresh()
  }

  private def toggleHiddenFiles() = {
    showHiddenFiles = !showHiddenFiles
    refresh()
  }

  private def actionOfContext(item: FileItem) = {
    (item.hasChildren, item.isImage) match {
      case (true, _) => getCurrentItem.map(_.toItem).foreach(cd)
      case (_, true) => getCurrentItem.map(_.toFileItem).foreach(viewImage)
      case (_, false) => getCurrentItem.map(_.toFileItem).foreach(viewText)
      case _ =>
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
          case (false, KeyCode.Enter) => getCurrentItem.map(_.toFileItem).foreach(actionOfContext)
          case (false, KeyCode.BackSpace) => currentLocation.getParent.foreach(cd)
          case (false, KeyCode.Period) => toggleHiddenFiles()
          case (false, KeyCode.BackSlash) => currentLocation.getRoot.foreach(cd)
          case (false, KeyCode.C) => getCurrentItem.map(_.toFileItem).foreach(copyFile)
          case (false, KeyCode.E) => getCurrentItem.map(_.toFileItem).foreach(editFile)
          case (true,  KeyCode.U) => getCurrentItem.map(_.toFileItem).filter(_.isArchive).foreach(cd)
          case (false, KeyCode.V) => getCurrentItem.map(_.toFileItem).foreach(viewText)
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
