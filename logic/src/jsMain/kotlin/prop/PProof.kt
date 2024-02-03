package prop

sealed class PProof {
    data class Box(
        val id: Int,
        val parent: Box?,
        val adj: Box?,
        val children: MutableList<PProof> = mutableListOf(),
    ) : PProof()

    data class Line(
        val id: Int,
        val parent: Box,
        val expr: PExpr,
        val just: PJust,
    ) : PProof()

    // fun availableSteps(): List<Pair<PExpr, >> TODO!!!!!
}
