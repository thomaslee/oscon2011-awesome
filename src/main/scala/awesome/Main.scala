package awesome

import java.io.FileInputStream

object Main {
  def main(args : Array[String]) = {
    val sourceFileName = args(0)
    val inputStream = new FileInputStream(sourceFileName)
    val parser = new Parser()
    val res = parser.parse(inputStream)
    inputStream.close()

    res match {
      case Left(err) => error(err)
      case Right(module) => {
        val codeGenerator = new CodeGenerator()
        codeGenerator.generateClass(sourceFileName, module)
      }
    }
  }
}

