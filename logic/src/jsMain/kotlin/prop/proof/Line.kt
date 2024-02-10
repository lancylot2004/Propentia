package prop.proof

import prop.Expression
import prop.Expression.*
import prop.InferenceRule
import prop.InferenceRule.*
import prop.proof.OptionTree.*

@JsExport
class Line(
    override val id: Int,
    override val parent: Box,
    val expr: Expression,
    val just: InferenceRule,
) : Proof() {
    override fun root(): Box = parent.root()

    /** Returns natural deduction steps that can be made _before_ this line. */
    fun availableSteps(): Array<Option> {
        val currScope = scope()
        val availableSteps = mutableListOf<Option>()

        // TopI does not require anything to be used on.
        availableSteps.add(Option(TopI::class))

        // ImpI, IffI, can be used on any pair.
        listOf(ImpI::class, IffI::class)
            .forEach {
                availableSteps.add(Option(it, Input() then Input()))
            }

        // EM, PC, can be used on anything.
        listOf(EM::class, PC::class)
            .forEach {
                availableSteps.add(Option(it, Input()))
            }

        // TODO: NotI can be used given a negated expression or by choosing one out of scope.

        if (currScope.isNotEmpty()) {
            // AndI can be used on any pair of existing lines.
            availableSteps.add(Option(AndI::class, AnyInScope() then AnyInScope()))

            // DNotI can be used on any existing line.
            availableSteps.add(Option(DNotI::class, AnyInScope()))

            // OrI can be used on an existing line and either another line or a user input.
            availableSteps.add(Option(OrI::class, AnyInScope() then Input(), Input() then AnyInScope()))
        }

        // For matching antecedents and negated consequents for ImpE and MT respectively.
        val imps = mutableSetOf<Line>()

        // For matching expressions in double implications for IffE.
        val iffs = mutableSetOf<Line>()

        // Process eliminations, and build up data for rest.
        currScope.forEach {
            // We can use [when] here since an expr will only be one type.
            when (it.expr) {
                is And -> {
                    // AndE can be used on any existing And to achieve either its lhs or rhs.
                    availableSteps.add(Option(AndE::class, Tree(it.id) then Choice(it.expr.lhs, it.expr.rhs)))
                }
                is Or -> {
                    // OrE can be used on any existing Or to achieve any result.
                    availableSteps.add(Option(OrE::class, Tree(it.id) then Input()))
                }
                is Bottom -> {
                    // BotE can be used on any existing Bottom to achieve any result.
                    availableSteps.add(Option(BotE::class, Input()))
                }
                is Not -> {
                    // DNotE can be used on any existing double negation.
                    if (it.expr.expr is Not) availableSteps.add(Option(DNotE::class, Tree(it.id)))
                }
                is Imp -> imps.add(it)
                is Iff -> {
                    // IffED can be used on any existing double implication.
                    availableSteps.add(Option(IffED::class, Tree(it.id)))

                    iffs.add(it)
                }
                else -> Unit
            }
        }

        // Second iteration, check rest of rules using collected data.
        currScope.forEach {
            // We cannot use [when] here because these tests potentially overlap.
            imps.forEach { imp ->
                if (it.expr == (imp.expr as Imp).ant) {
                    // ImpE can be used on an implication and its antecedent.
                    availableSteps.add(Option(ImpE::class, Tree(imp.id) then Tree(it.id)))
                }

                if (it.expr == Not(imp.expr.csq)) {
                    // MT can be used on an implication and the negation of its consequent.
                    availableSteps.add(Option(MT::class, Tree(imp.id) then Tree(it.id)))
                }
            }

            iffs.forEach { iff ->
                (iff.expr as Iff).let { iffExpr ->
                    if (it.expr == iffExpr.lhs || it.expr == iffExpr.rhs) {
                        // IffE can be used on any double implication and one of its sides.
                        availableSteps.add(Option(IffE::class, Tree(iff.id) then Tree(it.id)))
                    }
                }
            }

            currScope.forEach { other ->
                if (it.expr is Not && it.expr.expr == other.expr) {
                    // NotE can be used on any expression and its negation.
                    availableSteps.add(Option(NotE::class, Tree(other.id) then (Tree(it.id))))
                }
            }
        }

        return availableSteps
            .groupBy { it.infRule }
            .map {
                Option(
                    it.key,
                    it.value
                        .map { opt -> opt.optionTrees }
                        .toTypedArray()
                        .flatten()
                        .toTypedArray(),
                )
            }
            .toTypedArray()
    }
}
