package common

fun Boolean?.compareBy(other: Boolean?, comparator: (Boolean, Boolean) -> Boolean): Boolean? {
    return this?.let { lhsB -> other?.let { rhsB -> comparator(lhsB, rhsB) } }
}