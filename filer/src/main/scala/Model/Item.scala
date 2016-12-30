package Model

trait Item extends UniqueItem {
  def hasChildren: Boolean

  def parent: Option[Item]

  def root: Option[Item]
}

