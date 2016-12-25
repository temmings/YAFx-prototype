package Model.FileItem

import java.io.File
import java.net.URI
import java.nio.file.{Path, Paths}

import Configuration.Default
import Model.{Item, UniqueItem}
import org.apache.commons.vfs2.{FileContent, FileObject, FileSystemException}

case class FileItem(
                     file: FileObject,
                     alias: Option[String] = None,
                     SeparateExtensionMaxLength: Int = Default.SeparateExtensionMaxLength)
  extends {
    val id: String = file.getName.getURI
    val name: String = alias.getOrElse(file.getName.getBaseName)
    val ext: String = if (file.isFolder) "" else file.getName.getExtension
    val size: Long = if (file.isFile) file.getContent.getSize else 0
    val modifiedAt: Long = try file.getContent.getLastModifiedTime catch {
      case e: FileSystemException => println(e); 0
    }
  } with Item with UniqueItem with FileItemAttribute with FormatFileItem {

  override def hasChildren: Boolean = isDirectory || isVirtualDirectory

  override def getParent: Option[FileItem] = Option(file.getParent).map(x => this.copy(x))

  override def getRoot: Option[FileItem] = Option(file.getFileSystem.getRoot).map(x => this.copy(x))

  def getBaseName: String = file.getName.getBaseName

  def getContents: FileContent = file.getContent

  def toFile: File = new File(file.getName.getPath)

  def toPath: Path = Paths.get(file.getName.getPath)

  def toURI: URI = new URI(file.getName.getURI)
}
