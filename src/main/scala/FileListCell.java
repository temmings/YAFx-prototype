import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.File;

public class FileListCell extends ListCell<File> {
    private final Label name = new Label();
    private final Label size = new Label();
    private final Label modTime = new Label();
    private final HBox container = new HBox(8.0, name, size, modTime);

    public FileListCell() {
        name.setPrefWidth(440.0);
        size.setPrefWidth(100.0);
        size.setAlignment(Pos.CENTER_RIGHT);
        modTime.setPrefWidth(130.0);
        modTime.setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    protected void updateItem(File file, boolean empty) {
        super.updateItem(file, empty);
        if (null == file || empty) {
            name.setText("");
            size.setText("");
            modTime.setText("");
            setGraphic(null);
            return;
        }
        name.setText(file.getName());
        size.setText(Utils.getFileSizeString(file));
        modTime.setText(Utils.formatDateTime(file.lastModified()));
        setGraphic(container);
        if (file.isHidden()) {
            name.setStyle("-fx-text-fill: blue;");
            size.setStyle("-fx-text-fill: blue;");
            modTime.setStyle("-fx-text-fill: blue;");
        } else if (file.isDirectory()) {
            name.setStyle("-fx-text-fill: aqua;");
            size.setStyle("-fx-text-fill: aqua;");
            modTime.setStyle("-fx-text-fill: aqua;");
        } else {
            name.setStyle("-fx-text-fill: #DDDDDD;");
            size.setStyle("-fx-text-fill: #DDDDDD;");
            modTime.setStyle("-fx-text-fill: #DDDDDD;");
        }
    }
}