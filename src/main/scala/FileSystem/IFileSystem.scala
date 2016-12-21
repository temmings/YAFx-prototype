package FileSystem

import Model.ListFile

trait IFileSystem {
  def listFiles(): List[ListFile]
}
