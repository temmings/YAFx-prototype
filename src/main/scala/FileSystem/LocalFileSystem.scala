package FileSystem

import java.io.{FileInputStream, InputStream}
import java.nio.file.Path

import Model.FileItem

case class LocalFileSystem(path: Path) extends IFileSystem {
  def listFiles(relative: String): List[FileItem] = {
    val list = path.toFile.listFiles().toList
      .sortBy(f => (!f.isDirectory, f.getName))
      .map(f => FileItem(f))

    Option(path.getParent) match {
      case None => list
      case Some(p) => FileItem(p.toFile, Some("..")) :: list
    }
  }

  def getContents(name: String): InputStream = new FileInputStream(path.resolve(name).toFile)
}
