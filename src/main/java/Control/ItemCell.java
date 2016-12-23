package Control;

import Model.FormatItem;
import Utils.ItemCellUtil;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * see also: http://blog.livedoor.jp/fukai_yas/archives/47274295.html
 */
public class ItemCell extends ListCell<FormatItem> {
    private final HBox container = new HBox(0.0);

    @Override
    protected void updateItem(FormatItem item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item || empty) {
            setGraphic(null);
            return;
        }

        container.getChildren().clear();
        ItemCellUtil.append(container, item);
        Label head = (Label)container.getChildren().get(0);
        HBox.setHgrow(head, Priority.ALWAYS);
        ReadOnlyDoubleProperty headSize = container.widthProperty();
        container.getChildren().stream().filter(x -> x != head)
                .forEach(x -> headSize.subtract(((Label)x).widthProperty()));
        head.maxWidthProperty().bind(headSize);
        setGraphic(container);
    }
}