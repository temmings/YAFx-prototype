import Controller.{FileListController, ViewerController}

import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextArea, TextField}
import scalafx.scene.layout.{AnchorPane, HBox, VBox}

import Utils.ListFile


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

  val anchor = new AnchorPane {
    id = "mainPanel"
    prefHeight = Configuration.App.DefaultWindowHeight
    prefWidth = Configuration.App.DefaultWindowWidth
    stylesheets = Seq("stylesheet.css")
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
      }
    )
  }

  stage = new JFXApp.PrimaryStage() {
    title = "YAFx"
    scene = new Scene(anchor)
  }

  val viewerController = ViewerController(viewer)
  val fileListController = FileListController(location, fileList, viewerController)
  val fileListRightController = FileListController(locationRight, fileListRight, viewerController)
  fileListController.setPairFileListController(fileListRightController)
  fileListRightController.setPairFileListController(fileListController)
  Platform.runLater(fileList.requestFocus)
}
