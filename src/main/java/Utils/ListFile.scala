package Utils

import java.text.SimpleDateFormat

case class ListFile(file: java.io.File, name: String) {
  def realName = file.getName
  def size = file.length
  def sizeOrTypeString: String =
    if (file.isDirectory) "<DIR>"
    else file.length.toString
  def modifiedTime = new java.util.Date(file.lastModified)
  def modifiedTimeString = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(modifiedTime)
  def toFile = file
}
