package prop

typealias VarID = String

@JsExport
sealed class PExpr {
    data object Top : PExpr()

    data object Bottom : PExpr()

    data class Var(val id: VarID) : PExpr()

    data class Not(val expr: PExpr) : PExpr()

    data class And(val lhs: PExpr, val rhs: PExpr) : PExpr()

    data class Or(val lhs: PExpr, val rhs: PExpr) : PExpr()

    data class Imp(val ant: PExpr, val csq: PExpr) : PExpr()

    data class Iff(val lhs: PExpr, val rhs: PExpr) : PExpr()

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
            is Not -> expr.isAtomic()
            else -> false
        }
    }

    /** Is a disjunction of one or more literals. */
    fun isClause(): Boolean {
        if (this.isLiteral()) return true
        return when (this) {
            is Or -> lhs.isLiteral() && rhs.isLiteral()
            else -> false
        }
    }

    /** Is in *Negation Normal Form*, i.e., only contains conjunctions, disjunctions, and literals. */
    fun isNNF(): Boolean {
        return when (this) {
            is Var, Top, Bottom -> true
            is Not -> expr.isNNF()
            is And -> lhs.isNNF() && rhs.isNNF()
            is Or -> lhs.isNNF() && rhs.isNNF()
            else -> false
        }
    }

    /** Is in *Conjunctive Normal Form*, i.e., is a conjunction is disjunctions. */
    fun isCNF(): Boolean {
        return when (this) {
            is Var, Top, Bottom -> true
            is And -> lhs.isCNF() && rhs.isCNF()
            is Or ->
                arrayOf(lhs, rhs).all {
                    when (it) {
                        is Or -> it.lhs.isLiteral() && it.rhs.isLiteral()
                        else -> it.isLiteral()
                    }
                }
            else -> false
        }
    }

    /** Is in *Disjunctive Normal Form*, i.e., is a disjunction of conjunctions. */
    fun isDNF(): Boolean {
        return when (this) {
            is Var, Top, Bottom -> true
            is Or -> lhs.isDNF() && rhs.isDNF()
            is And ->
                arrayOf(lhs, rhs).all {
                    when (it) {
                        is And -> it.lhs.isLiteral() && it.rhs.isLiteral()
                        else -> it.isLiteral()
                    }
                }
            else -> false
        }
    }

    fun eval(env: Map<VarID, Boolean>): Boolean? { // TODO: Maps can't be type marshalled to js
        return when (this) {
            Top -> true
            Bottom -> false
            is Var -> env[id]
            is Not -> expr.eval(env)?.let { !it }
            is And -> lhs.eval(env).compareBy(rhs.eval(env), Boolean::and)
            is Or -> lhs.eval(env).compareBy(rhs.eval(env), Boolean::or)
            is Imp -> ant.eval(env).compareBy(csq.eval(env)) { antB, csqB -> !antB || csqB }
            is Iff -> lhs.eval(env).compareBy(rhs.eval(env), Boolean::equals)
        }
    }
}

fun Boolean?.compareBy(
    other: Boolean?,
    comparator: (Boolean, Boolean) -> Boolean,
): Boolean? {
    return this?.let { lhsB -> other?.let { rhsB -> comparator(lhsB, rhsB) } }
}
