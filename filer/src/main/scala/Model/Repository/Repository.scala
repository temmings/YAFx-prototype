package Model.Repository

import Model.Entity.Entity

trait Repository[E <: Entity] {
  def resolve(id: E#ID): Option[E]
}