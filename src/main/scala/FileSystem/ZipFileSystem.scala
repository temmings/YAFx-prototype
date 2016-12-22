package FileSystem

import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipFile

import Model.ListFile

import scala.collection.JavaConverters._

case class ZipFileSystem(path: Path) extends IFileSystem {
  def listFiles(relative: String): List[ListFile] = {
    val zip = new ZipFile(path.toFile)
    val entries = zip.stream().iterator().asScala
    val list = entries.map(x => ListFile.fromZipEntry(this, x)).toList
    ListFile.fromPath(this, path.getParent, Some("..")) :: list
  }

  def getContents(name: String): InputStream = {
    val zip = new ZipFile(path.toFile)
    val entries = zip.stream().iterator().asScala
    val entry = entries.find(_.getName == name.replace("\\","/")).get
    zip.getInputStream(entry)
  }
}
