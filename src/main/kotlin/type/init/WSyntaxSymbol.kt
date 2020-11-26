package type.init

import WInitBuiltins
import scope.WScope
import source.WSourceInfo
import type.*

data class WSyntaxSymbol(val name: String): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope): WValue {
        val headSymbol = WSymbol(name).also { it.sourceInfo = sourceInfo }
        return when {
            name in WInitBuiltins -> WInitBuiltins[name]
            headSymbol in scope -> scope[headSymbol]
            else -> headSymbol
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        throw IllegalArgumentException("Cannot invoke syntax symbol: $name")
    }

    override fun toString(): String = "#$name"
}