import java.io.File

object LocalFileSystem {
  def getList(f: File): List[File] = {
    if (!f.exists || !f.isDirectory)
      List[File]()

    f.listFiles()
      .sortBy(sortCondition)
      .toList
  }
  def getList(path: String): List[File] = getList(new File(path))

  def sortCondition(f: File) = (!f.isDirectory, f.getName)

  def isExist(path: String) = new File(path).exists
  def isFile(path: String) = new File(path).isFile
  def isDirectory(path: String) = new File(path).isDirectory
  def canChangeDirectory(file: File): Boolean = file.isDirectory && file.canRead
  def canChangeDirectory(path: String): Boolean = canChangeDirectory(new File(path))
  def canView(file: File) = !file.isDirectory && file.canRead
}
