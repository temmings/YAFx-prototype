package Configuration

// TODO: 何らかの設定ファイルからの読み込み (yaml あたり？ ruby/python あたりを組み込んでもいいかも)
object App {
  val editor = "C:/Users/temmings/Dropbox/Windows/vim74-kaoriya-win64/gvim.exe --remote-tab-silent"
  val defaultLocation = "/"
  val stylesheet = "stylesheet.css"
  val ViewerBufferSize = 1024 * 64
  val ViewerDefaultCharset = "utf-8"

  val DefaultFileColor = "#DDDDDD"
  val SystemFileColor = "darkorchid"
  val ReadOnlyFileColor = "red"
  val HiddenFileColor = "blue"
  val DirectoryColor = "aqua"

  val DefaultWindowHeight = 480.0
  val DefaultWindowWidth = 1024.0
}
