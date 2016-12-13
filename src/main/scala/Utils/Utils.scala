package Utils

import java.io.{File, FileInputStream}

/**
  * Created by temmings on 12/11/2016.
  */
object Utils {
  /**
    *  Guess whether given file is binary. Just checks for anything under 0x09.
    *  http://stackoverflow.com/questions/620993/determining-binary-text-file-type-in-java
    */
  def isBinaryFile(f: File): Boolean = {
    // TODO: detect UTF-16
    // TODO: detect BOM
    val in = new FileInputStream(f)
    val maxSize = 1024
    val size = if (in.available < maxSize) in.available else maxSize
    val data = new Array[Byte](size)
    in.read(data)
    in.close()

    var ascii = 0
    var other = 0

    for (i <- 0 to data.length-1) {
      val b = data(i)
      //println(f"$i: $b : $b%02X")
      if (0x00 <= b && b < 0x09) return true

      if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) ascii += 1
      else if (0x20 <= b && b <= 0x7E) ascii += 1
      else other += 1
    }

    if (other == 0) return false

    return 100 * other / (ascii + other) > 95
  }
}
