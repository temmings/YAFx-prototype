package Control;

import com.sun.jna.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

import Utils.NativeUtils;

/**
 * see also: http://blog.livedoor.jp/fukai_yas/archives/47274295.html
 */
public class FileListCell extends ListCell<ListFile> {
    private final Label name = new Label();
    private final Label ext = new Label();
    private final Label size = new Label();
    private final Label modTime = new Label();
    private final HBox container = new HBox(0.0, name, ext, size, modTime);

    public FileListCell() {
        size.setPrefWidth(100.0);
        size.setAlignment(Pos.CENTER_RIGHT);
        modTime.setPrefWidth(130.0);
        modTime.setAlignment(Pos.CENTER_RIGHT);
        ext.setPrefWidth(40.0);
        // TODO: calc name label size
        name.setPrefWidth(230.0);
    }

    @Override
    protected void updateItem(ListFile file, boolean empty) {
        super.updateItem(file, empty);
        if (null == file || empty) {
            setGraphic(null);
            return;
        }
        clearStyleClass();
        setGraphic(container);
        if (file.hasExtension()
                && 0 < file.nameWithoutExtension().length()
                && file.extension().length() <= Configuration.App.SeparateExtensionMaxLength()) {
            name.setText(file.nameWithoutExtension());
            ext.setText('.' + file.extension());
            ext.getStyleClass().add(String.format("yafx-file-ext-%s", file.extension()));
        } else {
            name.setText(file.name());
            ext.setText("");
        }
        size.setText(file.sizeOrTypeString());
        modTime.setText(file.modifiedTimeString());

        addStyleClass("yafx-file");
        if (file.toFile().isHidden()) addStyleClass("yafx-file-attr-hidden");
        if (!file.toFile().canWrite()) addStyleClass("yafx-file-attr-readonly");
        if (file.toFile().isDirectory()) addStyleClass("yafx-file-attr-directory");

        // Windows specific
        if (Platform.isWindows()) setWindowsItemColor(file.toFile());
    }

    private void setWindowsItemColor(File file) {
        DosFileAttributes attr;
        Boolean isJunction;
        try {
            attr = Files.readAttributes(file.toPath(), DosFileAttributes.class);
            isJunction = NativeUtils.isJunctionOrSymlink(file);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return;
        }
        if (file.isDirectory() && isJunction) size.setText("<JCT>");
        if (attr.isSystem()) addStyleClass("yafx-file-attr-win-system");
        if (attr.isReadOnly()) addStyleClass("yafx-file-attr-win-readonly");
        if (isJunction) addStyleClass("yafx-file-attr-win-junction");
    }

    private void addStyleClass(String name) {
        container.getChildren()
                .forEach(n -> n.getStyleClass().add(name));
    }

    private void clearStyleClass() {
        container.getChildren().forEach(n -> n.getStyleClass().clear());
    }
}