import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import scalafxml.core.macros.sfxml

@sfxml
class FileListController(
    val location: TextField,
    val list: ListView[String]) {

  val fs = LocalFileSystem
  val defaultLocation = "C:/"

  def initialize = {
    location.text.onChange {
      refreshFileList
    }
    setLocation(defaultLocation)
  }

  def setLocation(path: String) = {
    location.setText(path)
  }

  def refreshFileList = {
    if (fs.isDirectory(location.getText)) {
      list.items = ObservableBuffer(fs.getList(location.getText))
    }
  }

  initialize
}
