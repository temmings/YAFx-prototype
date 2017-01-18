package Configuration

import java.nio.charset.Charset

// TODO: 何らかの設定ファイルからの読み込み (yaml あたり？ ruby/python あたりを組み込んでもいいかも)
object Default {
  val Location: String = System.getProperty("user.home")
  val ViewerBufferSize: Int = 1024 * 64
  val ViewerCharset: Charset = Charset.defaultCharset()
  val SeparateExtensionMaxLength = 4

  val WindowHeight = 480.0
  val WindowWidth = 1024.0

  val MaxDirHistories = 100
  val SupportArchiveExtensions = List("gz", "bz2", "tar", "tgz", "tbz2", "zip", "jar", "lzh")
  val SupportImageExtensions = List("bmp", "jpg", "jpeg", "png", "gif")
}
