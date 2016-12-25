package Model

trait FormatItem extends Item {
  type Value = String
  type Style = String

  def getColumns: List[(Value, List[Style])]
  def getStyles: List[Style]
}