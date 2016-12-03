import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object Utils {
  def formatDateTime(time: Long) = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(time))

  def getFileSizeString(file: File) = {
    if (file.isDirectory)
      "<DIR>"
    else
      file.length.toString
  }
}
