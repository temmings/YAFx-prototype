package Control

import java.text.SimpleDateFormat

case class ListFile(file: java.io.File, name: String) {
  def hasExtension = !file.isDirectory && file.getName.contains('.')
  def realName = file.getName
  def nameWithoutExtension = if (hasExtension) file.getName.dropRight(1+extension.length) else name
  def extension = if (hasExtension) file.getName.split('.').last else ""
  def size = file.length
  def sizeOrTypeString: String =
    if (file.isDirectory) "<DIR>"
    else file.length.toString
  def modifiedTime = new java.util.Date(file.lastModified)
  def modifiedTimeString = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(modifiedTime)
  def toFile = file
}
