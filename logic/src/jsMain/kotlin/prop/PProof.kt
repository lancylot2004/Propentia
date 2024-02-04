@file:Suppress("ktlint:standard:no-wildcard-imports")

package prop

import prop.PExpr.*
import prop.PJust.*
import kotlin.reflect.KClass

@JsExport
sealed class PProof {
    abstract val id: Int
    abstract val parent: Box?

    abstract fun root(): Box

    data class Box(
        override val id: ID,
        override val parent: Box?,
        var adj: Box?,
        val idGen: IDGen,
        var children: Array<PProof> = arrayOf(),
    ) : PProof() {
        override fun root(): Box {
            var currRoot = this
            while (currRoot.parent != null) currRoot = currRoot.parent!!
            return currRoot
        }

        fun insertAfter(
            id: ID,
            newObj: PProof,
        ) {
            val ind = children.indexOfFirst { it.id == id }
            if (ind == -1) throw NoSuchElementException("Item with id $id does not exist in this proof!")
            children =
                children.toMutableList()
                    .apply { add(ind + 1, newObj) }
                    .toTypedArray()
        }

        fun insertBefore(
            id: ID,
            newObj: PProof,
        ) {
            val ind = children.indexOfFirst { it.id == id }
            if (ind == -1) throw NoSuchElementException("Item with id $id does not exist in this proof!")
            children =
                children.toMutableList()
                    .apply { add(ind, newObj) }
                    .toTypedArray()
        }

        /** Appends a [Line] to the end of [this]. Returns its [ID]. */
        fun appendLine(
            expr: PExpr,
            just: PJust,
        ): ID {
            val newID = idGen.getID()
            children += Line(newID, this, expr, just)
            return newID
        }

        /** Appends a [Box] to the end of [this]. Returns its [ID]. */
        fun appendBox(init: Box.() -> Unit) {
            val newBox = Box(idGen.getID(), this, null, idGen)
            newBox.init()
            children += newBox
        }

        /** Appends two parallel [Box]s to the end of [this]. Returns their [ID]s in an array. */
        fun appendParallel(initParallel: Box.(left: Box, right: Box) -> Unit): Array<ID> {
            val (leftID, rightID) = Pair(idGen.getID(), idGen.getID())
            val leftBox = Box(leftID, this, null, idGen)
            val rightBox = Box(rightID, this, leftBox, idGen)
            leftBox.adj = rightBox
            initParallel(leftBox, rightBox)
            children += leftBox
            children += rightBox
            return arrayOf(leftID, rightID)
        }
    }

    data class Line(
        override val id: Int,
        override val parent: Box,
        val expr: PExpr,
        val just: PJust,
    ) : PProof() {
        override fun root(): Box = parent.root()

        /** Returns natural deduction steps that can be made _before_ this line. */
        fun availableSteps(): Array<String> {
            val currScope = scope()
            val availableSteps = mutableSetOf<KClass<out PJust>>()

            // ImpI, NotI, TopI, IffI, EM, PC, can be used anywhere.
            availableSteps.addAll(arrayOf(ImpI::class, NotI::class, TopI::class, IffI::class, EM::class, PC::class))

            if (currScope.isNotEmpty()) {
                // AndI, OrI, DNotI, can be used on any existing line.
                availableSteps.addAll(arrayOf(AndI::class, OrI::class, DNotI::class))
            }

            // For matching arbitrary expressions.
            val exprs = mutableSetOf<PExpr>()

            // For matching antecedents for ImpE.
            val ants = mutableSetOf<PExpr>()

            // For matching negated consequents for MT.
            val notCsqs = mutableSetOf<PExpr>()

            // For matching expressions in double implications for IffE.
            val iffExprs = mutableSetOf<PExpr>()

            // Process eliminations, and build up data for rest.
            currScope.forEach {
                // Adds expression to test for not-phis. Inefficient!
                exprs.add(it.expr)

                // We can use [when] here since an expr will only be one type.
                when (it.expr) {
                    is And -> {
                        // AndE can be used on any existing And.
                        availableSteps.add(AndE::class)
                    }
                    is Or -> {
                        // OrE can be used on any existing Or.
                        availableSteps.add(OrE::class)
                    }
                    is Bottom -> {
                        // BotE can be used on any existing Bottom.
                        availableSteps.add(BotE::class)
                    }
                    is Not -> {
                        // DNotE can be used on any existing double negation.
                        if (it.expr.expr is Not) availableSteps.add(DNotE::class)
                    }
                    is Imp -> {
                        // Add for matching later.
                        ants.add(it.expr.ant)
                        notCsqs.add(Not(it.expr.csq))
                    }
                    is Iff -> {
                        // IffED can be used on any existing double implication.
                        availableSteps.add(IffED::class)

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
                    availableSteps.add(ImpE::class)
                }

                if (it.expr in iffExprs) {
                    // IffE can be used on a double implication and one of its sides.
                    availableSteps.add(IffE::class)
                }

                if (it.expr in notCsqs) {
                    // MT can be used on an implication and the negation of its consequent.
                    availableSteps.add(MT::class)
                }

                if (it.expr is Not && it.expr.expr in exprs) {
                    // NotE can be used on any expression and its negation.
                    availableSteps.add(NotE::class)
                }
            }

            return availableSteps
                .map { it.simpleName!! }
                .toTypedArray()
        }
    }

    fun scope(): Array<Line> {
        if (parent == null) return arrayOf()

        return parent!!
            .children
            .dropLastWhile { it != this }
            .flatMap { if (it is Line) listOf(it) else it.scope().asIterable() }
            .union(parent!!.scope().asIterable())
            .toTypedArray()
    }
}

@JsExport
class IDGen {
    private var currID: Int = 0

    fun getID(): Int = currID++
}
