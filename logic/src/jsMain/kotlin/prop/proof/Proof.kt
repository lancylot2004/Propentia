@file:Suppress("ktlint:standard:no-wildcard-imports")

package prop.proof

import prop.ID

@JsExport
sealed class Proof {
    abstract val id: ID
    abstract val parent: Box?

    abstract fun root(): Box

    companion object {
        fun new(init: Box.() -> Unit): Box {
            val box = IDGen().let { Box(it.getID(), null, null, it) }
            box.init()
            return box
        }
    }

    fun scope(): Array<Line> =
        when (parent) {
            null -> arrayOf()
            else ->
                parent!!
                    .children
                    .dropLastWhile { it != this }.drop(1) // Drop self and all after.
                    .filterIsInstance<Line>() // Ignore sibling boxes.
                    .union(parent!!.scope().asIterable()) // Recursively call scope upwards.
                    .toTypedArray()
        }
}

@JsExport
class IDGen {
    private var currID: ID = 0

    fun getID(): ID = currID++
}
