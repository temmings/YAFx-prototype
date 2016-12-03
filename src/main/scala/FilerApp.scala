import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}


object FilerApp extends JFXApp {
  val resource = getClass.getResource("FilerApp.fxml")
  val root = FXMLView(resource, NoDependencyResolver)

  stage = new JFXApp.PrimaryStage() {
    title = "FilerApp"
    scene = new Scene(root)
  }
}
