package Model

import java.io.File

trait FileItemAttribute {
  val file: File
  val ext: String

  val isDirectory: Boolean = file.isDirectory
  val isHidden: Boolean = file.isHidden || file.getName.startsWith(".")
  val isReadOnly: Boolean = !file.canWrite
  val isImage: Boolean = List("bmp", "jpg", "jpeg", "png", "gif").contains(ext.toLowerCase())
  val isArchive: Boolean = List("zip", "jar").contains(ext.toLowerCase())

  //private void setWindowsItemColor(ListFile item) {
  //    DosFileAttributes attr;
  //    Boolean isJunction;
  //    try {
  //        attr = Files.readAttributes(item.path(), DosFileAttributes.class);
  //        isJunction = NativeUtils.isJunctionOrSymlink(item.toFile());
  //    } catch (IOException ioe) {
  //        System.out.println(ioe.getMessage());
  //        return;
  //    }
  //    if (item.isDirectory() && isJunction) size.setText("<JCT>");
  //    if (attr.isSystem()) addStyleClass("yafx-file-attr-win-system");
  //    if (attr.isReadOnly()) addStyleClass("yafx-file-attr-win-readonly");
  //    if (isJunction) addStyleClass("yafx-file-attr-win-junction");
  //}
}
