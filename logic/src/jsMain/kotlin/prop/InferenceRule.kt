package prop

typealias ID = Int

@JsExport
sealed class InferenceRule {
    // === Abstracts ===

    /** Returns the justification tag of a proof step, e.g., "->E (3)". */
    abstract val tag: String

    /** Returns the justification tag in latex of a proof step. */
    abstract val latexTag: String

    /** Returns the human-readable description of what the proof step means. */
    abstract val desc: String

    data class AndI(val lhs: ID, val rhs: ID) : InferenceRule() {
        override val tag = "∧I"
        override val latexTag = "\\wedge I"
        override val desc = "Introduces the conjunct of two expressions already in scope."
    }

    data class AndE(val orig: ID) : InferenceRule() {
        override val tag = "∧E"
        override val latexTag = "\\wedge E"
        override val desc = "Eliminates a conjunct already in scope and extracts one of its conjuncts."
    }

    data class OrI(val orig: ID) : InferenceRule() {
        override val tag = "∨I"
        override val latexTag = "\\vee I"
        override val desc =
            "Introduces the disjunct of an expression already in scope and another arbitrary expression."
    }

    data class OrE(val orig: ID, val resL: ID, val resR: ID) : InferenceRule() {
        override val tag = "∨E"
        override val latexTag = "\\vee E"
        override val desc = "Eliminates a disjunct by proving that both logically entail the same resulting expression."
    }

    data class ImpI(val ant: ID, val csq: ID) : InferenceRule() {
        override val tag = "->I"
        override val latexTag = "\\rightarrow I"
        override val desc = "Introduces an implication of two sequential expressions in scope."

        // needs optional construct from antExpr and csqExpr
    }

    data class ImpE(val imp: ID, val ant: ID) : InferenceRule() {
        override val tag = "->E"
        override val latexTag = "\\rightarrow E"
        override val desc = "Eliminates an implication in scope given an implication and its antecedent in scope."
    }

    data class NotI(val phi: ID, val bot: ID) : InferenceRule() {
        override val tag = "¬I"
        override val latexTag = "\\neg I"
        override val desc =
            "Introduces an arbitrary negated expression by proving its positive logically entails bottom."

        // needs optional construct from desired not phi
    }

    // same as BotI
    data class NotE(val phi: ID, val notPhi: ID) : InferenceRule() {
        override val tag = "¬E/⊥I"
        override val latexTag = "\\neg E / \\perp I"
        override val desc = "Introduces a bottom given two conflicting expressions in scope."
    }

    data class BotE(val bot: ID) : InferenceRule() {
        override val tag = "⊥E"
        override val latexTag = "\\perp E"
        override val desc = "Eliminates a bottom in scope by deriving any arbitrary expression from it."
    }

    data class DNotI(val orig: ID) : InferenceRule() {
        override val tag = "¬¬I"
        override val latexTag = "\\neg\\neg I"
        override val desc = "Introduces the double negation of any expression in scope."
    }

    data class DNotE(val orig: ID) : InferenceRule() {
        override val tag = "¬¬E"
        override val latexTag = "\\neg\\neg E"
        override val desc = "Removes a double negation from any expression in scope."
    }

    data object TopI : InferenceRule() {
        override val tag = "⊤I"
        override val latexTag = "\\top I"
        override val desc = "Go ahead, introduce a top - wut r u gunna do 'bout it?"
    }

    data class IffI(val impLR: ID, val impRL: ID) : InferenceRule() {
        override val tag = "<->I"
        override val latexTag = "\\leftrightarrow I"
        override val desc = "Introduces a double implication given bidirectional implications in scope."

        // needs optional construct from lhs and rhs
    }

    data class IffE(val iff: ID, val phi: ID) : InferenceRule() {
        override val tag = "<->E"
        override val latexTag = "\\leftrightarrow E"
        override val desc =
            "Eliminates a double implication given a double implication and one of its nested expressions in scope."
    }

    data class IffED(val orig: ID) : InferenceRule() {
        override val tag = "<->E<D>"
        override val latexTag = "\\leftrightarrow E<D>"
        override val desc = "Transforms a double implication into a different form."
    }

    data object EM : InferenceRule() {
        override val tag = "E.M."
        override val latexTag = tag
        override val desc = "Introduces the disjunct of an arbitrary expression and its negation."
    }

    data class MT(val imp: ID, val notCsq: ID) : InferenceRule() {
        override val tag = "M.T."
        override val latexTag = tag
        override val desc =
            "Introduces the negated antecedent of an implication given it and the negated consequent in scope."
    }

    data class PC(val phi: ID, val bot: ID) : InferenceRule() {
        override val tag = "P.C."
        override val latexTag = tag
        override val desc: String =
            "Introduces an arbitrary expression by proving that its negation logically entails bottom."

        // needs optional construct from desired expression
    }

    data object Res : InferenceRule() {
        override val tag = "Res"
        override val latexTag = tag
        override val desc = "A placeholder tag for an expression that has not yet been proved."
    }

    data object Asm : InferenceRule() {
        override val tag = "Asm"
        override val latexTag = tag
        override val desc = "An assumption made to use a proof-step in-situ."
    }

    data object Prem : InferenceRule() {
        override val tag = "Prem"
        override val latexTag = tag
        override val desc = "An assumption which the proof is based upon."
    }

    data class Sta(val orig: ID) : InferenceRule() {
        override val tag = "Sta"
        override val latexTag = Prem.tag
        override val desc = "A previous expression repeated for clarity."
    }
}
