package FileSystem

import java.nio.file.Path
import java.util.zip.ZipFile

import scala.collection.JavaConverters._
import Model.ListFile

case class ZipFileSystem(path: Path) extends IFileSystem {
  private val file = path.toFile

  def listFiles(): List[ListFile] = {
    if (!file.exists) return Nil

    val list = new ZipFile(file).stream().iterator().asScala
      .map(x => ListFile.fromZipFile(file, x))
      .toList

    ListFile.fromPath(path.getParent, Some("..")) :: list
  }
}
