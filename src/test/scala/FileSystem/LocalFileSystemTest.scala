package FileSystem

import java.nio.file.Files

import org.scalatest.FunSuite

/**
  * Created by temmings on 12/22/2016.
  */
class LocalFileSystemTest extends FunSuite {

  test("list empty directory") {
    val t = Files.createTempDirectory("empty")
    t.toFile.deleteOnExit()
    val list = LocalFileSystem(t).listFiles()
    assert(list.nonEmpty)
    //assert(!list.exists(_.path != t.getParent))
  }

  test("list non empty directory") {
    val t = Files.createTempDirectory("not empty")
    t.toFile.deleteOnExit()
    val innerDir = Files.createTempDirectory(t, "inner dir")
    innerDir.toFile.deleteOnExit()
    val list = LocalFileSystem(t).listFiles()
    assert(list.nonEmpty)
    //assert(list.exists(_.path != t.getParent))
  }
}
