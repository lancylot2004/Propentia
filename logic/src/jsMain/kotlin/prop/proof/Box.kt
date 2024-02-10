package prop.proof

import prop.Expression
import prop.ID
import prop.InferenceRule

@JsExport
class Box(
    override val id: ID,
    override val parent: Box?,
    var adj: Box?,
    protected val idGen: IDGen,
    var children: Array<Proof> = arrayOf(),
) : Proof() {
    override fun root(): Box {
        var currRoot = this
        while (currRoot.parent != null) currRoot = currRoot.parent!!
        return currRoot
    }

    /** Appends a [Line] to the end of this [Box]. Returns its [ID]. */
    fun appendLine(
        expr: Expression,
        just: InferenceRule,
    ): ID {
        val newID = idGen.getID()
        children += Line(newID, this, expr, just)
        return newID
    }

    /** Appends a [Box] to the end of this [Box]. Returns its [ID]. */
    fun appendBox(init: Box.() -> Unit) {
        val newBox = Box(idGen.getID(), this, null, idGen)
        newBox.init()
        children += newBox
    }

    /** Appends two parallel [Box]s to the end of this [Box]. Returns their [ID]s in an array. */
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
