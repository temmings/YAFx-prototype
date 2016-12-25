package Util

import java.io.InputStream

import org.mozilla.universalchardet.UniversalDetector

object Utils {
  /**
    *  Guess whether given file is binary. Just checks for anything under 0x09.
    *  http://stackoverflow.com/questions/620993/determining-binary-text-file-type-in-java
    */
  def isBinary(in: InputStream): Boolean = {
    // TODO: detect UTF-16
    // TODO: detect BOM
    val maxSize: Int = 1024
    val size = if (in.available < maxSize) in.available else maxSize
    val data = new Array[Byte](size)
    in.read(data)

    var ascii = 0
    var other = 0

    for (i <- data.indices) {
      val b = data(i)
      //println(f"$i: $b : $b%02X")
      if (0x00 <= b && b < 0x09) return true

      if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) ascii += 1
      else if (0x20 <= b && b <= 0x7E) ascii += 1
      else other += 1
    }

    if (other == 0) return false

    100 * other / (ascii + other) > 95
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
