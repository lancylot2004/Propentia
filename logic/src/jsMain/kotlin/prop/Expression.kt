package prop

typealias VarID = String

@JsExport
sealed class Expression {
    data object Top : Expression()

    data object Bottom : Expression()

    data class Var(val id: VarID) : Expression()

    data class Not(val expr: Expression) : Expression()

    data class And(val lhs: Expression, val rhs: Expression) : Expression()

    data class Or(val lhs: Expression, val rhs: Expression) : Expression()

    data class Imp(val ant: Expression, val csq: Expression) : Expression()

    data class Iff(val lhs: Expression, val rhs: Expression) : Expression()

    infix fun and(rhs: Expression): Expression = And(this, rhs)

    infix fun or(rhs: Expression): Expression = Or(this, rhs)

    infix fun imp(rhs: Expression): Expression = Imp(this, rhs)

    infix fun iff(rhs: Expression): Expression = Iff(this, rhs)

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
}

fun Boolean?.compareBy(
    other: Boolean?,
    comparator: (Boolean, Boolean) -> Boolean,
): Boolean? {
    return this?.let { lhsB -> other?.let { rhsB -> comparator(lhsB, rhsB) } }
}
