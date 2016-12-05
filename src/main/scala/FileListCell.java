import com.sun.jna.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

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

        // Windows specific
        if (Platform.isWindows()) {
            DosFileAttributes attr;
            Boolean isJunction;
            try {
                attr = Files.readAttributes(file.toPath(), DosFileAttributes.class);
                isJunction = NativeUtils.isJunctionOrSymlink(file);
            } catch (IOException ioe) {
                return;
            }
            if (file.isDirectory() && isJunction) {
                size.setText("<JCT>");
            }
            if (attr.isSystem()) {
                name.setStyle("-fx-text-fill: darkorchid;");
                size.setStyle("-fx-text-fill: darkorchid;");
                modTime.setStyle("-fx-text-fill: darkorchid;");
                return;
            }
            if (attr.isReadOnly()) {
                name.setStyle("-fx-text-fill: red;");
                size.setStyle("-fx-text-fill: red;");
                modTime.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        if (file.isHidden()) {
            name.setStyle("-fx-text-fill: blue;");
            size.setStyle("-fx-text-fill: blue;");
            modTime.setStyle("-fx-text-fill: blue;");
            return;
        }
        if (!file.canWrite()) {
            name.setStyle("-fx-text-fill: red;");
            size.setStyle("-fx-text-fill: red;");
            modTime.setStyle("-fx-text-fill: red;");
            return;
        }
        if (file.isDirectory()) {
            name.setStyle("-fx-text-fill: aqua;");
            size.setStyle("-fx-text-fill: aqua;");
            modTime.setStyle("-fx-text-fill: aqua;");
            return;
        }
        name.setStyle("-fx-text-fill: #DDDDDD;");
        size.setStyle("-fx-text-fill: #DDDDDD;");
        modTime.setStyle("-fx-text-fill: #DDDDDD;");
    }
}