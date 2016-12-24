package FileSystem

import java.io.{FileInputStream, InputStream}
import java.nio.file.Path

import Model.FileItem

case class LocalFileSystem(path: Path) extends IFileSystem {
  def listFiles(relative: String): List[FileItem] = {
    val parent = Option(path.getParent).map(x => FileItem(x.toFile, Some("..")))

    val files = path.toFile.listFiles()
    if (null == files) return parent.toList

    parent.toList ++ files.toList
      .sortBy(f => (!f.isDirectory, f.getName))
      .map(f => FileItem(f))
  }

  def getContents(name: String): InputStream = new FileInputStream(path.resolve(name).toFile)
}
