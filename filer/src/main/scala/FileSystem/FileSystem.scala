package FileSystem

import Model.FileItem.FileItem
import org.apache.commons.vfs2.{FileNotFolderException, VFS}

case class FileSystem(path: String) extends IFileSystem {
  private val vfs = VFS.getManager
  private val scheme = Configuration.App.SupportArchiveExtensions.flatMap(x =>
    if (path.toLowerCase.endsWith(f".$x")) Some(f"$x:") else None).headOption.getOrElse("")
  private val file = vfs.resolveFile(scheme + path)

  def listFiles(): List[FileItem] = {
    val parent = Option(file.getParent).map(x => FileItem(x, Some(".."))).toList
    val files = try file.getChildren.toList catch {
      case e: FileNotFolderException =>
        println(e)
        Nil
    }

    parent ++ files
      .sortBy(f => (!f.isFolder, f.getName))
      .map(f => FileItem(f))
  }
}
