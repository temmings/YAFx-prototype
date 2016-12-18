import Controller._
import Model.ListFile

import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextArea, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, HBox, VBox}
import scalafx.scene.paint.Color


object YAFx extends JFXApp {

  val location = new TextField {
    editable = true
    prefHeight = 25.0
    prefWidth = Configuration.App.DefaultWindowWidth / 2
  }

  val fileList = new ListView[ListFile] {
    editable = false
    prefHeight = Configuration.App.DefaultWindowHeight - location.getPrefHeight
    prefWidth = Configuration.App.DefaultWindowWidth / 2
  }

  val locationRight = new TextField {
    editable = true
    prefHeight = 25.0
    prefWidth = Configuration.App.DefaultWindowWidth / 2
  }

  val fileListRight = new ListView[ListFile] {
    editable = false
    prefHeight = Configuration.App.DefaultWindowHeight - locationRight.getPrefHeight
    prefWidth = Configuration.App.DefaultWindowWidth / 2
  }

  val viewer = new TextArea {
    editable = false
    visible = false
    wrapText = true
    prefHeight = Configuration.App.DefaultWindowHeight
    prefWidth = Configuration.App.DefaultWindowWidth
  }

  val imageContainer = new HBox {
    visible = false
    style = "-fx-background-color: black"
    prefHeight = Configuration.App.DefaultWindowHeight
    prefWidth = Configuration.App.DefaultWindowWidth
    alignment = Pos.Center
  }

  val anchor = new AnchorPane {
    id = "mainPanel"
    prefHeight = Configuration.App.DefaultWindowHeight
    prefWidth = Configuration.App.DefaultWindowWidth
    stylesheets = Seq("basic.css", "custom.css")
    children = Seq(
      new HBox {
        alignment = Pos.Center
        fillHeight = true
        children = Seq(
          new VBox {
            alignment = Pos.Center
            fillWidth = true
            children = Seq(location, fileList)
          },
          new VBox {
            alignment = Pos.Center
            fillWidth = true
            children = Seq(locationRight, fileListRight)
          })
      },
      new HBox {
        alignment = Pos.Center
        fillHeight = true
        children = viewer
      },
      imageContainer
    )
  }

  stage = new JFXApp.PrimaryStage() {
    title = "YAFx"
    scene = new Scene(anchor)
  }

  val viewerController = new ViewerController(viewer)
  val imageViewController = new ImageViewController(imageContainer)
  val fileListController = new FileListController(location, fileList, viewerController, imageViewController)
  val fileListRightController = new FileListController(locationRight, fileListRight, viewerController, imageViewController)
  fileListController.setPairFileListController(fileListRightController)
  fileListRightController.setPairFileListController(fileListController)
  Platform.runLater(fileList.requestFocus)
}
