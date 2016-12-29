package Model.Repository

import Configuration.Default
import Model.Entity.{History, HistoryId}

// TODO: ファイルに履歴を保存して再利用できるようにする
class HistoryRepository extends Repository[History] {
  var histories: List[History] = Nil
  def resolve(id: HistoryId): Option[History] = {
    println(f"find $id")
    histories.find(_.id == id)
  }

  def create(value: History): Unit = {
    println(f"create $value")
    histories = histories.filter(_.id != value.id)
    histories = value :: histories.take(Default.MaxDirHistories)
    println(histories)
  }
}
