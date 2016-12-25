package Model.Entity

trait Entity {
  type ID
  def id: ID
  override final def equals(obj: Any): Boolean = obj match {
    case other: Entity => other.canEqual(this) && this.id == other.id
    case _ => false
  }
  override final def hashCode: Int = id.hashCode
  def canEqual(other: Any): Boolean = other.getClass == this.getClass
}
