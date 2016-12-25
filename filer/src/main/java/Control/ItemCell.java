package Control;

import Model.FormatItem;
import Util.ItemCellUtil;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * see also: http://blog.livedoor.jp/fukai_yas/archives/47274295.html
 */
public class ItemCell extends ListCell<FormatItem> {
    private final BorderPane container = new BorderPane();

    @Override
    protected void updateItem(FormatItem item, boolean empty) {
        super.updateItem(item, empty);
        container.getChildren().clear();

        if (null == item || empty) {
            setGraphic(null);
            return;
        }
        setGraphic(container);

        List<Label> labels = ItemCellUtil.getLabels(item);
        Label head = labels.get(0);
        container.setLeft(head);

        HBox hbox = new HBox(0.0);
        labels.subList(1, labels.size()).forEach(x -> hbox.getChildren().add(x));
        container.setRight(hbox);

        // TODO: bind hbox label size
        head.maxWidthProperty().bind(container.widthProperty().subtract(270));

        //container.getChildren().addAll(ItemCellUtil.getLabels(item));
        //Label head = (Label)container.getChildren().get(0);
        //HBox.setHgrow(head, Priority.ALWAYS);
        //ReadOnlyDoubleProperty headSize = container.widthProperty();
        //container.getChildren().stream().filter(x -> x != head)
        //        .forEach(x -> headSize.subtract(((Label)x).widthProperty()));
        //head.maxWidthProperty().bind(headSize);
    }
}