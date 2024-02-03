package prop

import kotlin.js.JsExport

typealias Env = Map<String, Boolean>

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

    /** Is a propositional atom, top, or bottom. (Excl. negated atomics.) */
    fun isAtomic(): Boolean {
        return when (this) {
            is Var, Top, Bottom -> true
            else -> false
        }
    }

    /** Is either an atomic or a negated-atomic */
    fun isLiteral(): Boolean {
        if (this.isAtomic()) return true
        return when (this) {
            is Not -> this.expr.isAtomic()
            else -> false
        }
    }

    /** Is a disjunction of one or more literals.) */
    fun isClause(): Boolean {
        if (this.isLiteral()) return true
        return when (this) {
            is Or -> left.isLiteral() && right.isLiteral()
            else -> false
        }
    }

    /** Evaluates a [PExpr] with a given environment, returning [null] if insufficient information. */
    fun eval(env: Env): Boolean? {
        return when (this) {
            is Var -> env[name]
            is Top -> true
            is Bottom -> false
            is Not -> expr.eval(env)?.not()
            is And -> left.eval(env) == true && right.eval(env) == true
            is Or -> left.eval(env) == true || right.eval(env) == true
            is Imp -> left.eval(env) == false || right.eval(env) == true
            is Iff -> left.eval(env) == right.eval(env)
        }
    }
}
