import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}


object YAFx extends JFXApp {
  val resource = getClass.getResource("YAFx.fxml")
  val root = FXMLView(resource, NoDependencyResolver)

  stage = new JFXApp.PrimaryStage() {
    title = "YAFx"
    scene = new Scene(root)
  }
}
