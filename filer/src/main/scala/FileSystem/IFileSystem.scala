package FileSystem

import Model.FileItem.FileItem

trait IFileSystem {
  def listFiles(): List[FileItem]
}
