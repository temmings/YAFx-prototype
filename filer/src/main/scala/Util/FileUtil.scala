package Util

import Model.FileItem.FileItem
import org.apache.commons.vfs2.VFS

object FileUtil {
  private val vfs = VFS.getManager

  def getFile(path: String): FileItem = FileItem(vfs.resolveFile(path))
}
