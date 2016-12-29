package Controller

import java.io.PrintStream

import Model.OutputStream.TextAreaOutputStream

import scalafx.scene.control.TextArea

class ConsoleController(textArea: TextArea) {
  private val console = new TextAreaOutputStream(textArea)
  private val ps = new PrintStream(console, true)
  System.setOut(ps)
  System.setErr(ps)
  System.err.flush()
  System.out.flush()
}

