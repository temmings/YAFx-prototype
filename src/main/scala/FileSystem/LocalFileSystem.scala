package FileSystem

import java.io.File

import Control.ListFile

case class LocalFileSystem() {
  def getList(f: File): List[ListFile] = {
    if (!f.exists || !f.isDirectory)
      return List[ListFile]()

    val list = f.listFiles()
        .sortBy(sortCondition)
        .map(f => ListFile(f, f.getName))
        .toList

    if (null == f.getParentFile)
      return list

    return List[ListFile](ListFile(f.getParentFile, "..")) ++ list
  }
  def getList(path: String): List[ListFile] = getList(new File(path))

  def sortCondition(f: File) = (!f.isDirectory, f.getName)

  // Utilities
  def isExist(path: String) = new File(path).exists
  def isFile(path: String) = new File(path).isFile
  def isDirectory(path: String) = new File(path).isDirectory
  def canChangeDirectory(file: File): Boolean = file.isDirectory && file.canRead
  def canChangeDirectory(path: String): Boolean = canChangeDirectory(new File(path))
  def canView(file: File) = !file.isDirectory && file.canRead
}
