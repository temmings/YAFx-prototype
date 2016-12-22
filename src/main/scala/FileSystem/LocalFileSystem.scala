package FileSystem

import java.io.{File, FileInputStream, InputStream}
import java.nio.file.Path

import Model.ListFile

case class LocalFileSystem(path: Path) extends IFileSystem {
  def listFiles(relative: String): List[ListFile] = {
    val list = path.resolve(relative).toFile.listFiles()
        .sortBy(f => (!f.isDirectory, f.getName))
        .map(f => ListFile.fromPath(this, f.toPath.getFileName))
        .toList

    Option(path.getParent) match {
      case None => list
      case Some(p) => ListFile.fromPath(this, p, Some("..")) :: list
    }
  }

  def getContents(name: String): InputStream = new FileInputStream(path.resolve(name).toFile)
}
