package Model

trait FormatItem {
  type Value = String
  type Style = String

  def getColumns: List[(Value, List[Style])]
  def getStyles: List[Style]

  def toItem: Item = this.asInstanceOf[Item]
  def toFileItem: FileItem = this.asInstanceOf[FileItem]
}