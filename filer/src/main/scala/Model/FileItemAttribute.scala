package Model

import org.apache.commons.vfs2.FileObject

trait FileItemAttribute {
  val file: FileObject
  val ext: String
  val alias: Option[String]

  val isDirectory: Boolean = file.isFolder
  val isHidden: Boolean = (file.isHidden || file.getName.getBaseName.startsWith(".")) && alias.getOrElse("") != ".."
  val isReadOnly: Boolean = !file.isWriteable
  val isImage: Boolean = Configuration.App.SupportImageExtensions.contains(ext.toLowerCase())
  val isVirtualDirectory: Boolean = Configuration.App.SupportArchiveExtensions.contains(ext.toLowerCase())
}
