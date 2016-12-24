package Model

import java.io.File
import java.nio.file.attribute.DosFileAttributes
import java.nio.file.{Files, Paths}

import Utils.NativeUtils
import org.apache.commons.vfs2.FileObject

trait WindowsFileItemAttribute extends FileItemAttribute {
  private val attr = Files.readAttributes(Paths.get(file.getName.getPath), classOf[DosFileAttributes])

  val file: FileObject
  override val isReadOnly: Boolean = attr.isReadOnly
  val isSystem: Boolean = attr.isSystem
  val isJunctionOrSymlink: Boolean = NativeUtils.isJunctionOrSymlink(new File(file.getName.getPath))


  //if (file.isDirectory() && isJunction) size.setText("<JCT>");
  //if (attr.isSystem()) addStyleClass("yafx-file-attr-win-system");
  //if (attr.isReadOnly()) addStyleClass("yafx-file-attr-win-readonly");
  //if (isJunction) addStyleClass("yafx-file-attr-win-junction");
}
