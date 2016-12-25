import Configuration.Default
import Controller._
import Model.FormatItem

import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextArea, TextField}
import scalafx.scene.layout._


object YAFx extends JFXApp {
  val viewer = new TextArea {
    editable = false
    visible = false
    wrapText = true
    styleClass += "yafx-content-viewer"
  }

  val imageContainer = new HBox {
    visible = false
    styleClass += "yafx-image-container"
  }

  val locationLeft = new TextField {
    editable = true
    styleClass += "yafx-location-bar"
  }
  val listLeft = new ListView[FormatItem] {
    editable = false
    vgrow = Priority.Always
    styleClass += "yafx-content-list"
  }

  val locationRight = new TextField {
    editable = true
    styleClass += "yafx-location-bar"
  }
  val listRight = new ListView[FormatItem] {
    editable = false
    vgrow = Priority.Always
    styleClass += "yafx-content-list"
  }

  val listContainer = new HBox {
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

  val pane = new StackPane {
    stylesheets = Seq("basic.css", "custom.css")
    children = Seq(listContainer, viewer, imageContainer)
  }

  stage = new JFXApp.PrimaryStage() {
    title = "YAFx"
    minHeight = 200
    minWidth = 400
    maxHeight = Double.PositiveInfinity
    maxWidth = Double.PositiveInfinity
    height = Default.WindowHeight
    width = Configuration.Default.WindowWidth
    scene = new Scene(pane)
  }

  val viewerController = new TextViewerController(viewer)
  val imageViewController = new ImageViewController(imageContainer)
  val fileListController = new ListController(locationLeft, listLeft, viewerController, imageViewController)
  val fileListRightController = new ListController(locationRight, listRight, viewerController, imageViewController)
  fileListController.setPairFileListController(fileListRightController)
  fileListRightController.setPairFileListController(fileListController)
  Platform.runLater(listLeft.requestFocus)
}
