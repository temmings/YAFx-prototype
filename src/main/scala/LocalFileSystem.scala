import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object LocalFileSystem {
  def getList(path: String) = {
    val f = new File(path)

    if (!f.exists || !f.isDirectory)
      List[String]()

    f.listFiles
      .sortWith(sortFileList)
      .map(formatFileProperty)
      .toList
  }

  def sortFileList(a: File, b: File) = {
    if (a.isDirectory && !b.isDirectory) true
    else a.getName < b.getName
  }

  def formatFileProperty(file: File) = {
    if (file.isDirectory)
      f"${file.getName}%-65s ${"<DIR>"}%11s ${formatDateTime(file.lastModified)}"
    else
      f"${file.getName}%-65s ${file.length}%11d ${formatDateTime(file.lastModified)}"
  }

  def formatDateTime(time: Long) = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(time))

  def isExist(path: String) = new File(path).exists
  def isFile(path: String) = new File(path).isFile
  def isDirectory(path: String) = new File(path).isDirectory
}
