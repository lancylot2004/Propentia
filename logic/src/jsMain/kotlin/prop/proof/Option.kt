package prop.proof

import prop.Expression
import prop.ID
import prop.InferenceRule
import kotlin.reflect.KClass

@JsExport
class Option(
    val infRule: String,
    val optionTrees: Array<OptionTree>,
) {
    @JsExport.Ignore
    constructor(
        infRule: KClass<out InferenceRule>,
        vararg optionTrees: OptionTree,
    ) : this(infRule.simpleName!!, optionTrees.toList().toTypedArray())
}

@JsExport
sealed class OptionTree {
    var thenLines: Array<OptionTree> = arrayOf()

    data class Tree(val onLine: ID) : OptionTree()

    data object AnyInScope : OptionTree()

    data object AnyOutOfScope : OptionTree()

    data object Input : OptionTree()

    data class Choice(val exprs: Array<Expression>) : OptionTree() {
        @JsExport.Ignore
        constructor(vararg exprs: Expression) : this(exprs.toList().toTypedArray())
    }

    infix fun then(option: OptionTree): OptionTree {
        this.thenLines += option
        return this
    }

    infix fun thens(options: Array<OptionTree>): OptionTree {
        this.thenLines += options
        return this
    }
}
