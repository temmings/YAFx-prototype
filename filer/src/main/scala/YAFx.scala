import Configuration.Default
import Controller._
import Model.FormatItem

import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextArea, TextField}
import scalafx.scene.layout._


object YAFx extends JFXApp {
  val viewer = new TextArea {
    id = "yafx-content-viewer"
    editable = false
    visible = false
    wrapText = true
  }

  val imageContainer = new HBox {
    id = "yafx-image-container"
    visible = false
  }

  val locationLeft = new TextField {
    id = "yafx-location-bar"
    editable = true
    mouseTransparent = true
  }
  val listLeft = new ListView[FormatItem] {
    id = "yafx-content-list"
    editable = false
    mouseTransparent = true
    vgrow = Priority.Always
  }

  val locationRight = new TextField {
    id = "yafx-location-bar"
    editable = true
    mouseTransparent = true
  }
  val listRight = new ListView[FormatItem] {
    id = "yafx-content-list"
    editable = false
    mouseTransparent = true
    vgrow = Priority.Always
  }

  val listBox = new HBox {
    children = Seq(
      new VBox {
        children = Seq(locationLeft, listLeft)
        hgrow = Priority.Always
      },
      new VBox {
        children = Seq(locationRight, listRight)
        hgrow = Priority.Always
      })
  }

  val mainPane = new StackPane {
    children = Seq(listBox, viewer, imageContainer)
  }

  val console = new TextArea {
    id = "yafx-console"
    editable = false
    wrapText = true
    mouseTransparent = true
    margin = Insets(16, 0, 0, 0)
  }

  val window = new BorderPane {
    center = mainPane
    bottom = console
  }

  stage = new JFXApp.PrimaryStage {
    title = "YAFx"
    minHeight = 200
    minWidth = 400
    maxHeight = Double.PositiveInfinity
    maxWidth = Double.PositiveInfinity
    height = Default.WindowHeight
    width = Configuration.Default.WindowWidth
    scene = new Scene(window) {
      stylesheets = Seq("basic.css", "custom.css")
    }
  }

  val consoleController = new ConsoleController(console)
  val viewerController = new TextViewerController(viewer)
  val imageViewController = new ImageViewController(imageContainer)
  val fileListController = new ListController(locationLeft, listLeft, viewerController, imageViewController)
  val fileListRightController = new ListController(locationRight, listRight, viewerController, imageViewController)
  fileListController.setPairFileListController(fileListRightController)
  fileListRightController.setPairFileListController(fileListController)
  Platform.runLater(listLeft.requestFocus)
}
