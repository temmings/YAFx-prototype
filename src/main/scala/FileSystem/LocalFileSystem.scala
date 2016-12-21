package FileSystem

import java.nio.file.Path

import Model.ListFile

case class LocalFileSystem(path: Path) extends IFileSystem {
  private val file = path.toFile

  def listFiles(): List[ListFile] = {
    if (!file.exists || !file.isDirectory) return Nil

    val list = file.listFiles()
        .sortBy(f => (!f.isDirectory, f.getName))
        .map(f => ListFile.fromFile(f))
        .toList

    Option(path.getParent) match {
      case Some(p) => ListFile.fromPath(p, Some("..")) :: list
      case None => list
    }
  }
}
