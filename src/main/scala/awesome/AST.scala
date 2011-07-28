package awesome

trait Expr {}

case class Module(expressions : List[Expr])
case class BinOp(op : String, left : Expr, right : Expr) extends Expr
case class Num(value : Int) extends Expr

