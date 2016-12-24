package Model

import java.text.SimpleDateFormat

trait FormatFileItem extends FormatItem {
  val splitExtLength: Int
  val name: String
  val ext: String
  val size: Long
  val modifiedAt: Long
  val isDirectory: Boolean
  val isHidden: Boolean
  val isReadOnly: Boolean

  override def getColumns: List[(Value, List[Style])] = List(
    (getSplitName, List("yafx-item-name")),
    (getSplitExt.getOrElse(""), "yafx-item-ext" :: getSplitExt
      .toList.map(_.toLowerCase).map(x => s"yafx-item-ext-$x")),
    (getSizeOrType, List("yafx-item-size")),
    (getModifiedAtString, List("yafx-item-time"))
  )
  override def getStyles: List[Style] = {
    var result = List("yafx-item-file")
    if (isDirectory) result ::= "yafx-item-directory"
    if (isHidden) result ::= "yafx-item-attr-hidden"
    if (isReadOnly) result ::= "yafx-item-attr-readonly"
    result
  }

  private def getSizeOrType = if (isDirectory) "<DIR>" else size.toString
  private def getModifiedAtString = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(modifiedAt)
  private def getSplitName = {
    (isDirectory, getSplitExt) match {
      case (false, Some(x)) => name.dropRight(1 + x.length)
      case _ => name
    }
  }
  private def getSplitExt: Option[String] = {
    if (ext.length <= splitExtLength && ext.length < name.length-1) Some(ext) else None
  }
}