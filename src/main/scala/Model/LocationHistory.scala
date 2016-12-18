package Model

import java.nio.file.Path

// TODO: Pathは抽象化したクラスにする
case class LocationHistory(location: Path, focus: ListFile)
