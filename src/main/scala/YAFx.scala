import java.io.File

import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextArea, TextField}
import scalafx.scene.layout.{AnchorPane, HBox, VBox}


object YAFx extends JFXApp {

  val location = new TextField {
    editable = true
    prefHeight = 25.0
    prefWidth = Configuration.DefaultWindowWidth / 2
  }

  val fileList = new ListView[File] {
    editable = false
    prefHeight = Configuration.DefaultWindowHeight
    prefWidth = Configuration.DefaultWindowWidth / 2
  }

  val locationRight = new TextField {
    editable = true
    prefHeight = 25.0
    prefWidth = Configuration.DefaultWindowWidth / 2
  }

  val fileListRight = new ListView[File] {
    editable = false
    prefHeight = Configuration.DefaultWindowHeight
    prefWidth = Configuration.DefaultWindowWidth / 2
  }

  val viewer = new TextArea {
    editable = false
    visible = false
    wrapText = true
    prefHeight = Configuration.DefaultWindowHeight
    prefWidth = Configuration.DefaultWindowWidth
  }

  val anchor = new AnchorPane {
    id = "mainPanel"
    prefHeight = Configuration.DefaultWindowHeight
    prefWidth = Configuration.DefaultWindowWidth
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

  val fileListController = new FileListController(location, fileList, viewer)
  val fileListRightController = new FileListController(locationRight, fileListRight, viewer)
  val viewerController = new ViewerController(viewer, fileList)
}
