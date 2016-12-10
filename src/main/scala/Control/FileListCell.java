package Control;

import Utils.NativeUtils;
import com.sun.jna.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

import Utils.Utils;

public class FileListCell extends ListCell<File> {
    private final Label name = new Label();
    private final Label size = new Label();
    private final Label modTime = new Label();
    private final HBox container = new HBox(8.0, name, size, modTime);

    public FileListCell() {
        size.setPrefWidth(100.0);
        size.setAlignment(Pos.CENTER_RIGHT);
        modTime.setPrefWidth(130.0);
        modTime.setAlignment(Pos.CENTER_RIGHT);
        // TODO: calc name label size
        double nameWidth =
                Configuration.App.DefaultWindowWidth() / 2
                        - size.getPrefWidth()
                        - modTime.getPrefWidth()
                        - container.getSpacing() * (1 + container.getChildren().size());
        name.setPrefWidth(nameWidth);
    }

    @Override
    protected void updateItem(File file, boolean empty) {
        super.updateItem(file, empty);
        if (null == file || empty) {
            setGraphic(null);
            return;
        }
        setGraphic(container);
        name.setText(file.getName());
        size.setText(Utils.getFileSizeString(file));
        modTime.setText(Utils.formatDateTime(file.lastModified()));

        // Windows specific
        if (Platform.isWindows()) {
            if (setWindowsItemColor(file)) return;
        }

        if (file.isHidden()) {
            setItemColor(Configuration.App.HiddenFileColor());
            return;
        }
        if (!file.canWrite()) {
            setItemColor(Configuration.App.ReadOnlyFileColor());
            return;
        }
        if (file.isDirectory()) {
            setItemColor(Configuration.App.DirectoryColor());
            return;
        }
        setItemColor(Configuration.App.DefaultFileColor());
    }

    private Boolean setWindowsItemColor(File file) {
        DosFileAttributes attr;
        Boolean isJunction;
        try {
            attr = Files.readAttributes(file.toPath(), DosFileAttributes.class);
            isJunction = NativeUtils.isJunctionOrSymlink(file);
        } catch (IOException ioe) {
            return true;
        }
        if (file.isDirectory() && isJunction) {
            size.setText("<JCT>");
        }
        if (attr.isSystem()) {
            setItemColor(Configuration.App.SystemFileColor());
            return true;
        }
        if (attr.isReadOnly()) {
            setItemColor(Configuration.App.ReadOnlyFileColor());
            return true;
        }
        return false;
    }

    private void setItemColor(String color) {
        container.getChildren()
                .forEach(n -> n.setStyle(String.format("-fx-text-fill: %s;", color)));
    }
}