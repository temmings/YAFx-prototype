package FileSystem

import java.io.{File, FileInputStream}
import java.util.zip.{ZipEntry, ZipFile, ZipInputStream}
import scala.collection.JavaConverters._

import Model.ListFile

case class ZipFileSystem(file: File) {
  def getList(f: File): List[ListFile] = {
    if (!f.exists) return List[ListFile]()

    val list = new ZipFile(file).stream().iterator().asScala
      .map(x => ListFile.fromZipFile(file, x, x.getName))
      .toList

    ListFile.fromFile(file.getParentFile, "..") :: list
  }
  // Utilities
  def isExist(path: String): Boolean = new File(path).exists
  def isFile(path: String): Boolean = new File(path).isFile
  def isDirectory(path: String): Boolean = new File(path).isDirectory
  def canChangeDirectory(file: File): Boolean = file.isDirectory && file.canRead
  def canChangeDirectory(path: String): Boolean = canChangeDirectory(new File(path))
  def canView(file: File): Boolean = !file.isDirectory && file.canRead
}
