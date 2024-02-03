package prop

import kotlin.js.JsExport

@JsExport
sealed class PExpr {
    data object Top : PExpr()

    data object Bottom : PExpr()

    data class Var(val name: String) : PExpr()

    data class Not(val expr: PExpr) : PExpr()

    data class And(val left: PExpr, val right: PExpr) : PExpr()

    data class Or(val left: PExpr, val right: PExpr) : PExpr()

    data class Imp(val left: PExpr, val right: PExpr) : PExpr()

    data class Iff(val left: PExpr, val right: PExpr) : PExpr()

    public fun isAtomic(): Boolean {
        return when (this) {
            is Var, Top, Bottom -> true
            else -> false
        }
    }
}
