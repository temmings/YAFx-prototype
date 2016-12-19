import Controller._
import Model.ListFile

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
  val listLeft = new ListView[ListFile] {
    editable = false
    vgrow = Priority.Always
    styleClass += "yafx-content-list"
  }

  val locationRight = new TextField {
    editable = true
    styleClass += "yafx-location-bar"
  }
  val listRight = new ListView[ListFile] {
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
    height = Configuration.App.DefaultWindowHeight
    width = Configuration.App.DefaultWindowWidth
    scene = new Scene(pane)
  }

  val viewerController = new ViewerController(viewer)
  val imageViewController = new ImageViewController(imageContainer)
  val fileListController = new FileListController(locationLeft, listLeft, viewerController, imageViewController)
  val fileListRightController = new FileListController(locationRight, listRight, viewerController, imageViewController)
  fileListController.setPairFileListController(fileListRightController)
  fileListRightController.setPairFileListController(fileListController)
  Platform.runLater(listLeft.requestFocus)
}
