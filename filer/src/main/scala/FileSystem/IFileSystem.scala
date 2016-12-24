package FileSystem

import Model.FileItem

trait IFileSystem {
  def listFiles(): List[FileItem]
}
