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
    protected void updateItem(File file, boolean empty){
        super.updateItem(file, empty);
        if (null == file || empty) {
            name.setText("");
            size.setText("");
            modTime.setText("");
            setGraphic(container);
            return;
        }
        name.setText(file.getName());
        size.setText(LocalFileSystem.getFileSizeString(file));
        modTime.setText(LocalFileSystem.formatDateTime(file.lastModified()));
        setGraphic(container);
    }
}