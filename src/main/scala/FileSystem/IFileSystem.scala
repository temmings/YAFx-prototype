package FileSystem

import java.io.InputStream

import Model.FileItem

trait IFileSystem {
  def listFiles(relative: String = ""): List[FileItem]
  def getContents(name: String): InputStream
}
