package Utils

import javafx.scene.control.Label

import Model.FormatItem

import scala.collection.JavaConverters._

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
