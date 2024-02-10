package prop.proof

import prop.Expression
import prop.InferenceRule
import kotlin.reflect.KClass

@JsExport
class Line(
    override val id: Int,
    override val parent: Box,
    val expr: Expression,
    val just: InferenceRule,
) : Proof() {
    override fun root(): Box = parent.root()

    /** Returns natural deduction steps that can be made _before_ this line. */
    fun availableSteps(): Array<String> {
        val currScope = scope()
        val availableSteps = mutableSetOf<KClass<out InferenceRule>>()

        // ImpI, NotI, TopI, IffI, EM, PC, can be used anywhere.
        availableSteps.addAll(
            arrayOf(
                InferenceRule.ImpI::class,
                InferenceRule.NotI::class,
                InferenceRule.TopI::class,
                InferenceRule.IffI::class,
                InferenceRule.EM::class,
                InferenceRule.PC::class,
            ),
        )

        if (currScope.isNotEmpty()) {
            // AndI, OrI, DNotI, can be used on any existing line.
            availableSteps.addAll(arrayOf(InferenceRule.AndI::class, InferenceRule.OrI::class, InferenceRule.DNotI::class))
        }

        // For matching arbitrary expressions.
        val exprs = mutableSetOf<Expression>()

        // For matching antecedents for ImpE.
        val ants = mutableSetOf<Expression>()

        // For matching negated consequents for MT.
        val notCsqs = mutableSetOf<Expression>()

        // For matching expressions in double implications for IffE.
        val iffExprs = mutableSetOf<Expression>()

        // Process eliminations, and build up data for rest.
        currScope.forEach {
            // Adds expression to test for not-phis. Inefficient!
            exprs.add(it.expr)

            // We can use [when] here since an expr will only be one type.
            when (it.expr) {
                is Expression.And -> {
                    // AndE can be used on any existing And.
                    availableSteps.add(InferenceRule.AndE::class)
                }
                is Expression.Or -> {
                    // OrE can be used on any existing Or.
                    availableSteps.add(InferenceRule.OrE::class)
                }
                is Expression.Bottom -> {
                    // BotE can be used on any existing Bottom.
                    availableSteps.add(InferenceRule.BotE::class)
                }
                is Expression.Not -> {
                    // DNotE can be used on any existing double negation.
                    if (it.expr.expr is Expression.Not) availableSteps.add(InferenceRule.DNotE::class)
                }
                is Expression.Imp -> {
                    // Add for matching later.
                    ants.add(it.expr.ant)
                    notCsqs.add(Expression.Not(it.expr.csq))
                }
                is Expression.Iff -> {
                    // IffED can be used on any existing double implication.
                    availableSteps.add(InferenceRule.IffED::class)

                    // Add both LHS and RHS for matching later.
                    iffExprs.add(it.expr.lhs)
                    iffExprs.add(it.expr.rhs)
                }
                else -> Unit
            }
        }

        // Second iteration, check rest of rules using collected data.
        currScope.forEach {
            // We cannot use [when] here because these tests potentially overlap.
            if (it.expr in ants) {
                // ImpE can be used on an implication and its antecedent.
                availableSteps.add(InferenceRule.ImpE::class)
            }

            if (it.expr in iffExprs) {
                // IffE can be used on a double implication and one of its sides.
                availableSteps.add(InferenceRule.IffE::class)
            }

            if (it.expr in notCsqs) {
                // MT can be used on an implication and the negation of its consequent.
                availableSteps.add(InferenceRule.MT::class)
            }

            if (it.expr is Expression.Not && it.expr.expr in exprs) {
                // NotE can be used on any expression and its negation.
                availableSteps.add(InferenceRule.NotE::class)
            }
        }

        return availableSteps
            .map { it.simpleName!! }
            .toTypedArray()
    }
}
