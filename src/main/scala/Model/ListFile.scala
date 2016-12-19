package Model

import java.io.File
import java.nio.file.{Path, Paths}
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry

case class ListFile(
                     name: String,
                     realName: String,
                     path: Path,
                     size: Long,
                     modifiedTime: Date,
                     isDirectory: Boolean,
                     isHiddenFile: Boolean) {
  def hasExtension: Boolean = !isDirectory && name.contains('.')
  def nameWithoutExtension: String = if (hasExtension) name.dropRight(1+extension.length) else name
  def extension: String = if (hasExtension) name.split('.').last else ""
  def sizeOrTypeString: String = if (isDirectory) "<DIR>" else size.toString
  def modifiedTimeString: String = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(modifiedTime)
  def isHidden: Boolean = isHiddenFile || realName.startsWith(".")
  def isImageFile: Boolean = List("bmp", "jpg", "jpeg", "png", "gif").contains(extension.toLowerCase())
  def isArchive: Boolean = List("zip").contains(extension.toLowerCase())
  def toFile: File = path.toFile
}

object ListFile {
  def fromFile(f: File, alias: String): ListFile =
    ListFile(alias, f.getName, f.toPath,
      f.length, new Date(f.lastModified), f.isDirectory, f.isHidden)
  def fromZipFile(f: File, ze: ZipEntry, alias: String): ListFile =
    ListFile(alias, ze.getName, Paths.get(ze.getName),
      ze.getSize, new Date(ze.getTime), ze.isDirectory, isHiddenFile = false)
}
