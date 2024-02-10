package prop

import prop.Expression.Var
import prop.InferenceRule.*

/** p | q |- (p -> q) -> q*/
@JsExport
fun e1(): Proof =
    Proof.new {
        var (impID, csqID) = Pair(0, 0)
        val orID = appendLine(Var("p") or Var("q"), Prem)
        appendBox {
            impID = appendLine(Var("p") imp Var("q"), Asm)
            val (leftID, rightID) =
                appendParallel { left, right ->
                    val pID = left.appendLine(Var("p"), Asm)
                    left.appendLine(Var("q"), ImpE(impID, pID))

                    val qID = right.appendLine(Var("q"), Asm)
                    right.appendLine(Var("q"), Sta(qID))
                }
            csqID = appendLine(Var("q"), OrE(orID, leftID, rightID))
        }
        appendLine((Var("p") imp Var("q")) imp Var("q"), ImpI(impID, csqID))
    }
