package Model

trait Item extends UniqueItem {
  def hasChildren: Boolean
  def getParent: Option[Item]
  def getRoot: Option[Item]
}

