package Control

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

case class ListFile(file: File, name: String) {
  def hasExtension: Boolean = !file.isDirectory && file.getName.contains('.')
  def realName: String = file.getName
  def nameWithoutExtension: String = if (hasExtension) file.getName.dropRight(1+extension.length) else name
  def extension: String = if (hasExtension) file.getName.split('.').last else ""
  def size: Long = file.length
  def sizeOrTypeString: String =
    if (file.isDirectory) "<DIR>"
    else file.length.toString
  def modifiedTime: Date = new Date(file.lastModified)
  def modifiedTimeString: String = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(modifiedTime)
  def toFile: File = file
  def isHidden: Boolean = file.isHidden || file.getName.startsWith(".")
}
