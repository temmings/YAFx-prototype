package FileSystem

import java.io.InputStream

import Model.ListFile

trait IFileSystem {
  def listFiles(relative: String = ""): List[ListFile]
  def getContents(name: String): InputStream
}
