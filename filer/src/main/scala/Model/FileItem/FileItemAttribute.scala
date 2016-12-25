package Model.FileItem

import Configuration.Default

trait FileItemAttribute {
  self: FileItem =>

  val isDirectory: Boolean = file.isFolder
  val isHidden: Boolean = (file.isHidden || file.getName.getBaseName.startsWith(".")) && alias.getOrElse("") != ".."
  val isReadOnly: Boolean = !file.isWriteable
  val isImage: Boolean = Default.SupportImageExtensions.contains(ext.toLowerCase())
  val isVirtualDirectory: Boolean = Configuration.Default.SupportArchiveExtensions.contains(ext.toLowerCase())
}
