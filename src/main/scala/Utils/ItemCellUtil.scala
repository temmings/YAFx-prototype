package Utils

import scala.collection.JavaConverters._
import javafx.scene.control.Label

import Model.FormatItem

object ItemCellUtil {
  def getLabels(item: FormatItem): java.util.List[Label] = {
    item.getColumns.map {
      case (value, styles) =>
        val l = new Label(value)
        item.getStyles.foreach(l.getStyleClass.add)
        styles.foreach(l.getStyleClass.add)
        l
    }.asJava
  }
}
