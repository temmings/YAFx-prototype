package Repository

import Entity.{History, HistoryId}

class HistoryRepository extends Repository[History] {
  var histories: List[History] = Nil
  def resolve(id: HistoryId): Option[History] = {
    println(f"find $id")
    histories.find(_.id == id)
  }


  def create(value: History): Unit = {
    println(f"create $value")
    histories = histories.filter(_.id != value.id)
    histories = value :: histories.take(Configuration.App.MaxDirHistories)
    println(histories)
  }
}
