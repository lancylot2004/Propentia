package prop

import prop.PProof.Box

fun newProof(init: Box.() -> Unit): Box {
    val box = IDGen().let { Box(it.getID(), null, null, it) }
    box.init()
    return box
}
