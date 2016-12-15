package Configuration

// TODO: 何らかの設定ファイルからの読み込み (yaml あたり？ ruby/python あたりを組み込んでもいいかも)
object App {
  val Editor = "C:/Users/temmings/Dropbox/Windows/vim74-kaoriya-win64/gvim.exe --remote-tab-silent"
  val DefaultLocation:String = System.getProperty("user.home")
  val ViewerBufferSize:Int = 1024 * 64
  val ViewerDefaultCharset = "utf-8"
  val SeparateExtensionMaxLength = 4

  val DefaultWindowHeight = 480.0
  val DefaultWindowWidth = 1024.0
}
