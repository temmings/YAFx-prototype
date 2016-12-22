package FileSystem

import java.nio.file.Paths

import org.scalatest.FunSuite

/**
  * Created by temmings on 12/22/2016.
  */
class ZipFileSystemTest extends FunSuite {
  private val classLoader = getClass.getClassLoader

  ignore("list zip entries") {
    val t = Paths.get(classLoader.getResource("test.zip").toURI)
    val list = ZipFileSystem(t).listFiles()
    assert(list.nonEmpty)
    assert(list.exists(_.name == "apple"))
    assert(list.exists(_.name == "pen"))
    assert(list.exists(_.name == "pineapple"))
    assert(list.exists(_.name == "uh"))
  }

  test("list inner zip directories") {
    val t = Paths.get(classLoader.getResource("test.zip").toURI)
    val list = ZipFileSystem(t).listFiles("/uh")
    assert(list.nonEmpty)
    assert(list.exists(_.name == "apple-pen"))
    assert(list.exists(_.name == "pineapple-pen"))
  }

}
