package Utils

import javafx.scene.control.Label
import javafx.scene.layout.Pane

import Model.FormatItem

/**
  * Created by temmings on 12/23/2016.
  */
object ItemCellUtil {
  def append(container: Pane, item: FormatItem): Unit = {
    item.getColumns.foreach {
      case (value, style) =>
        val l = new Label()
        container.getChildren.add(l)
        item.getStyles.foreach(l.getStyleClass.add)
        style.foreach(l.getStyleClass.add)
        l.setText(value)
      case _ =>
    }
  }
}
