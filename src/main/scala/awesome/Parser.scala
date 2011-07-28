package awesome

import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

import scala.util.parsing.combinator.RegexParsers

class Parser extends RegexParsers {
  def program = ((expression <~ ";")*) ^^ { case expressions => Module(expressions) }

  def expression = sum

  def sum = (product ~ ((("+" | "-") ~ product)*)) ^^ { case head ~ tail =>
    foldBinOps(head, tail)
  }

  def product = (number ~ ((("*" | "/") ~ number)*)) ^^ { case head ~ tail =>
    foldBinOps(head, tail)
  }

  def number = ("[0-9]+" r) ^^ { case value => Num(Integer.parseInt(value)) }

  def foldBinOps(head : Expr, tail : List[String ~ Expr]) : Expr = {
    tail.foldLeft(head)((left, right) => BinOp(right._1, left, right._2) )
  }

  def parse(inputStream : InputStream) : Either[String, Module] = {
    val reader = new BufferedReader(new InputStreamReader(inputStream))
    return parseAll(program, reader) match {
      case Success(module, _) => Right(module)
      case NoSuccess(err, _) => Left(err)
    }
  }
}

