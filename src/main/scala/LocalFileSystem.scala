import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object LocalFileSystem {
  def getList(path: String): List[File] = {
    val f = new File(path)

    if (!f.exists || !f.isDirectory)
      List[File]()

    f.listFiles
      .sortBy(sortCondition)
      .toList
  }

  def sortCondition(f: File) = (!f.isDirectory, f.getName)

  def formatDateTime(time: Long) = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(time))

  def getFileSizeString(file: File) = {
    if (file.isDirectory)
      "<DIR>"
    else
      file.length.toString
  }

  def isExist(path: String) = new File(path).exists
  def isFile(path: String) = new File(path).isFile
  def isDirectory(path: String) = new File(path).isDirectory
}
