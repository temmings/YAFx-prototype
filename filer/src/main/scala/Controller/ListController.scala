package Controller

import java.awt.Desktop
import java.nio.file.{Files, Paths, StandardCopyOption}

import Control.ItemCell
import Entity.{History, HistoryId}
import FileSystem.{FileSystem, FileSystemUtil}
import Model._
import Repository.HistoryRepository

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}

class ListController(
                      location: TextField,
                      list: ListView[FormatItem],
                      viewer: TextViewerController,
                      imageViewer: ImageViewController) {
  var currentLocation: Item = FileSystemUtil.getFile(Configuration.App.DefaultLocation)
  private var pairFileList: ListController = _
  private var showHiddenFiles: Boolean = false
  private val histories = new HistoryRepository

  location.setMouseTransparent(true)
  location.onKeyPressed = onLocationKeyPressed

  list.setMouseTransparent(true)
  list.cellFactory = (_: ListView[FormatItem]) => new ListCell[FormatItem](new ItemCell())
  list.onKeyPressed = onListKeyPressed
  list.onKeyReleased = onListKeyReleased

  setLocation(currentLocation)

  def setPairFileListController(c: ListController): Unit = pairFileList = c

  private def setLocation(item: Item): Unit = {
    getCurrentItem.foreach(x => {
      histories.create(History(HistoryId(currentLocation.id), x))
    })

    currentLocation = item
    location.setText(item.id)
    refresh()

    histories.resolve(HistoryId(currentLocation.id)) match {
      case Some(History(_, focus)) =>
        println(f"focus: $focus")
        list.getSelectionModel.select(focus)
        list.scrollTo(focus)
      case _ =>
        list.getSelectionModel.selectFirst()
        list.scrollTo(0)
    }
  }
  private def undoLocation() = location.setText(currentLocation.id)

  private def getCurrentItem = Option(list.getSelectionModel.getSelectedItem)
  private def refresh() = {
    def filter(item: FileItem) = showHiddenFiles || !item.isHidden

    val items = FileSystem(currentLocation.id).listFiles()
    list.items = ObservableBuffer(items.filter(filter))
  }

  private def focusToLocationBar() = {
    println(s"focus to $location")
    location.requestFocus
    location.deselect()
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
    println(s"viewImage:($item)")
    imageViewer.open(list, item)
  }

  private def cd(item: Item) = {
    println(s"cd:($item)")
    setLocation(item)
  }

  private def copyFile(item: FileItem) = {
    Files.copy(
      item.toPath,
      Paths.get(pairFileList.currentLocation.id).resolve(item.getBaseName),
      StandardCopyOption.COPY_ATTRIBUTES)
    pairFileList.refresh()
  }

  private def toggleHiddenFiles() = {
    showHiddenFiles = !showHiddenFiles
    refresh()
  }

  private def actionOfContext(item: Item) = {
    if (item.hasChildren)
      getCurrentItem.foreach(cd)
    else item match {
      case x: FileItem =>
        if (x.isImage)
          viewImage(x)
        else
          viewText(x)
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
          case (false, KeyCode.Enter) => getCurrentItem.foreach(actionOfContext)
          case (false, KeyCode.BackSpace) => currentLocation.getParent.foreach(cd)
          case (false, KeyCode.Period) => toggleHiddenFiles()
          case (false, KeyCode.BackSlash) => currentLocation.getRoot.foreach(cd)
          case (false, KeyCode.C) => getCurrentItem.foreach {
            case x: FileItem => copyFile(x)
          }
          case (false, KeyCode.E) => getCurrentItem.foreach {
            case x: FileItem => editFile(x)
          }
          case (true, KeyCode.U) => getCurrentItem.filter(_.hasChildren).foreach(cd)
          case (false, KeyCode.V) => getCurrentItem.foreach {
            case x: FileItem => viewText(x)
          }
          case (false, KeyCode.Q) => closeApp()
          case _ =>
        }
    }
  }
  private def onListKeyReleased(e: KeyEvent) = {
    println(s"Released: ${e.code} on ${e.target} from ${e.source}")
    e.consume
    e.code match {
      case KeyCode.Comma => focusToLocationBar()
      case _ =>
    }
  }

  private def onLocationKeyPressed(e: KeyEvent) = {
    println(s"Pressed: ${e.code} on ${e.target} from ${e.source}")
    e.code match {
      case KeyCode.Escape =>
        e.consume
        undoLocation()
        focusToList()
      case KeyCode.Enter =>
        e.consume
        setLocation(FileSystemUtil.getFile(location.getText))
        focusToList()
      case KeyCode.Tab =>
        e.consume
      case _ =>
    }
  }
}
