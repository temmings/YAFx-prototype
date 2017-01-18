package Util

import java.io.InputStream

import org.mozilla.universalchardet.UniversalDetector

object Utils {
  def isBinary(in: InputStream): Boolean = {
    // TODO: detect UTF-16
    // TODO: detect BOM
    val size: Int = 1024
    val buf = new Array[Byte](size)
    val length = in.read(buf)
    val data = buf.take(length)

    if (data.exists((0x00 to 0x08).contains(_))) return true
    val asciiBytes = (0x09 to 0x0A) ++ (0x0C to 0x0D) ++ (0x20 to 0x7E)
    (100 * data.count(asciiBytes.contains)) / length <= 5
  }

  def detectCharset(in: InputStream): Option[String] = {
    try {
      val detector = new UniversalDetector(null)
      val buf = new Array[Byte](4096)
      var n = 0
      do {
        n = in.read(buf)
        if (0 < n) detector.handleData(buf, 0, n)
      } while (0 < n && !detector.isDone)
      detector.dataEnd()
      val charset = Option(detector.getDetectedCharset)
      detector.reset()

      charset
    } catch {
      case e: Exception => println(e); None
    }
  }
}
