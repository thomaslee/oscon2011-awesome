package awesome

import java.io.File

import org.apache.bcel.Constants
import org.apache.bcel.generic._

class CodeGenerator {
  def generateClass(sourceFileName : String, module : Module) = {
    //
    // public class HelloWorld extends java.lang.Object
    //
    val cg = new ClassGen(
      classNameFor(sourceFileName),
      "java.lang.Object",
      sourceFileName,
      Constants.ACC_PUBLIC,
      Array()
    )

    //
    // public static void main(String[] args)
    //
    val mg = new MethodGen(
      Constants.ACC_PUBLIC | Constants.ACC_STATIC,
      Type.VOID,
      Array(new ArrayType(Type.STRING, 1)),
      Array("args"),
      "main",
      cg.getClassName(),
      new InstructionList(),
      cg.getConstantPool()
    )
    val il = mg.getInstructionList()
    val f = new InstructionFactory(cg)

    visitModule(il, f, module)
    il.append(InstructionFactory.createReturn(Type.VOID))

    mg.setMaxStack()
    cg.addMethod(mg.getMethod())

    //
    // Write the class file.
    //
    cg.getJavaClass().dump(classFileNameFor(sourceFileName))
  }

  def visitModule(il : InstructionList, f : InstructionFactory, module : Module) = {
    module.expressions foreach { expr =>
      il.append(f.createGetStatic("java.lang.System", "out", new ObjectType("java.io.PrintStream")))
      visitExpr(il, f, expr)
      il.append(f.createInvoke("java.io.PrintStream", "println", Type.VOID, Array(Type.INT), Constants.INVOKEVIRTUAL))
    }
  }

  def visitExpr(il : InstructionList, f : InstructionFactory, expr : Expr) : Unit = {
    expr match {
      case binop : BinOp => visitBinOp(il, f, binop)
      case num : Num => visitNum(il, f, num)
    }
  }

  def visitBinOp(il : InstructionList, f : InstructionFactory, binop : BinOp) = {
    visitExpr(il, f, binop.left)
    visitExpr(il, f, binop.right)
    binop.op match {
      case "+" => il.append(new IADD())
      case "-" => il.append(new ISUB())
      case "*" => il.append(new IMUL())
      case "/" => il.append(new IDIV())
    }
  }

  def visitNum(il : InstructionList, f : InstructionFactory, num : Num) = {
    il.append(f.createConstant(num.value))
  }

  /*
   * "examples/HelloWorld.awesome" -> "examples.HelloWorld"
   */
  def classNameFor(sourceFileName : String) : String =
    sourceFileName.replaceAll("\\.awesome$", "").replaceAll(File.separator, ".")

  /*
   * "examples/HelloWorld.awesome" -> "examples/HelloWorld.class"
   */
  def classFileNameFor(sourceFileName : String) : String =
    sourceFileName.replaceAll("\\.awesome$", ".class")

}

