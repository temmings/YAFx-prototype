package FileSystem

import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipFile

import Model.FileItem

import scala.collection.JavaConverters._

case class ZipFileSystem(path: Path) extends IFileSystem {
  def listFiles(relative: String): List[FileItem] = {
    val zip = new ZipFile(path.toFile)
    val entries = zip.stream().iterator().asScala
    val list = entries.map(x => FileItem.fromZipEntry(x)).toList
    FileItem(path.getParent.toFile, Some("..")) :: list
  }

  def getContents(name: String): InputStream = {
    val zip = new ZipFile(path.toFile)
    val entries = zip.stream().iterator().asScala
    val entry = entries.find(_.getName == name.replace("\\","/")).get
    zip.getInputStream(entry)
  }
}
