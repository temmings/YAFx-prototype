package Control;

import Model.ListFile;
import com.sun.jna.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

import Utils.NativeUtils;
import javafx.scene.layout.Priority;

/**
 * see also: http://blog.livedoor.jp/fukai_yas/archives/47274295.html
 */
public class FileListCell extends ListCell<ListFile> {
    private final Label name = new Label();
    private final Label ext = new Label();
    private final Label size = new Label();
    private final Label time = new Label();
    private final HBox container = new HBox(0.0, name, ext, size, time);

    public FileListCell() {
        HBox.setHgrow(name, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(ListFile item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item || empty) {
            setGraphic(null);
            return;
        }
        clearStyleClass();
        setBasicStyleClass();
        name.maxWidthProperty().bind(
                container.widthProperty()
                        .subtract(ext.widthProperty())
                        .subtract(size.widthProperty())
                        .subtract(time.widthProperty())
        );
        setGraphic(container);
        if (item.hasExtension()
                && 0 < item.nameWithoutExtension().length()
                && item.extension().length() <= Configuration.App.SeparateExtensionMaxLength()) {
            name.setText(item.nameWithoutExtension());
            ext.setText('.' + item.extension());
            ext.getStyleClass().add(String.format("yafx-file-ext-%s", item.extension()));
        } else {
            name.setText(item.name());
            ext.setText("");
        }
        size.setText(item.sizeOrTypeString());
        time.setText(item.modifiedTimeString());

        addStyleClass("yafx-file");
        if (item.isHidden()) addStyleClass("yafx-file-attr-hidden");
        if (!item.toFile().canWrite()) addStyleClass("yafx-file-attr-readonly");
        if (item.isDirectory()) addStyleClass("yafx-file-attr-directory");

        // Windows specific
        if (Platform.isWindows()) setWindowsItemColor(item);
    }

    private void setWindowsItemColor(ListFile item) {
        DosFileAttributes attr;
        Boolean isJunction;
        try {
            attr = Files.readAttributes(item.path(), DosFileAttributes.class);
            isJunction = NativeUtils.isJunctionOrSymlink(item.toFile());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return;
        }
        if (item.isDirectory() && isJunction) size.setText("<JCT>");
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

    private void setBasicStyleClass() {
        name.getStyleClass().add("yafx-item-name");
        ext.getStyleClass().add("yafx-item-ext");
        size.getStyleClass().add("yafx-item-size");
        time.getStyleClass().add("yafx-item-time");
    }
}