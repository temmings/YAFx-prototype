package Model.FileItem

trait FileItemAttribute {
  self: FileItem =>

  val isDirectory: Boolean = file.isFolder
  val isHidden: Boolean = (file.isHidden || file.getName.getBaseName.startsWith(".")) && alias.getOrElse("") != ".."
  val isReadOnly: Boolean = !file.isWriteable
  val isImage: Boolean = Configuration.App.SupportImageExtensions.contains(ext.toLowerCase())
  val isVirtualDirectory: Boolean = Configuration.App.SupportArchiveExtensions.contains(ext.toLowerCase())
}
