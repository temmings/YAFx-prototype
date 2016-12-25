package Model.Entity

import Model.FormatItem

case class HistoryId(id: String)

case class History(id: HistoryId, item: FormatItem) extends Entity {
  type ID = HistoryId
}
