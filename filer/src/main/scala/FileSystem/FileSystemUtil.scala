package FileSystem

import Model.FileItem.FileItem
import org.apache.commons.vfs2.VFS

object FileSystemUtil {
  private val vfs = VFS.getManager

  def getFile(path: String): FileItem = FileItem(vfs.resolveFile(path))
}
