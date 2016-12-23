package Model

import java.io.{File, FileInputStream}
import java.net.URI
import java.nio.file.Path
import java.util.zip.ZipEntry

import org.apache.commons.io.FilenameUtils

case class FileItem(file: File, alias: Option[String] = None, splitExtLength: Int = 4)
  extends {
    val id: String = file.toPath.toString
    val name: String = alias.getOrElse(file.getName)
    val ext: String = if (file.isDirectory) "" else FilenameUtils.getExtension(file.getName)
    val size: Long = file.length
    val modifiedAt: Long = file.lastModified
  } with Item with FormatFileItem with FileItemAttribute {

  override def hasChildren: Boolean = isDirectory || isArchive
  override def getParent: Option[FileItem] = Option(file.getParentFile).map(x => this.copy(x))
  override def getRoot: Option[FileItem] = Option(file.toPath.getRoot.toFile).map(x => this.copy(x))

  def toFile: File = file
  def toPath: Path = file.toPath
  def toUri: URI = file.toURI
  def getContents: FileInputStream = new FileInputStream(file)
}

object FileItem {
  // TODO: implement
  def fromZipEntry(ze: ZipEntry) = ???
}