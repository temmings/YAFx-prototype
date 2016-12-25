package Configuration

import java.nio.charset.Charset

// TODO: 何らかの設定ファイルからの読み込み (yaml あたり？ ruby/python あたりを組み込んでもいいかも)
object App {
  val DefaultLocation: String = System.getProperty("user.home")
  val ViewerBufferSize: Int = 1024 * 64
  val ViewerDefaultCharset: Charset = Charset.defaultCharset()
  val SeparateExtensionMaxLength = 4

  val DefaultWindowHeight = 480.0
  val DefaultWindowWidth = 1024.0

  val MaxDirHistories = 100
  val SupportArchiveExtensions = List("gz", "bz2", "tar", "tgz", "tbz2", "zip", "jar")
  val SupportImageExtensions = List("bmp", "jpg", "jpeg", "png", "gif")
}
