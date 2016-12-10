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
    prefWidth = 720.0
  }

  val fileList = new ListView[File] {
    editable = false
    prefHeight = 480.0
    prefWidth = 720.0
  }

  val viewer = new TextArea {
    editable = false
    visible = false
    wrapText = true
    prefHeight = 480.0
    prefWidth = 720.0
  }

  val anchor = new AnchorPane {
    id = "mainPanel"
    prefHeight = 480.0
    prefWidth = 720.0
    stylesheets = Seq("stylesheet.css")
    children = Seq(
      new HBox {
        alignment = Pos.Center
        fillHeight = true
        children = new VBox {
          alignment = Pos.Center
          fillWidth = true
          children = Seq(location, fileList)
        }
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
  val viewerController = new ViewerController(viewer, fileList)
}
