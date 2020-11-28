package type.init

import WInitBuiltins
import scope.WScope
import source.WSourceInfo
import type.*

data class WSyntaxSymbol(val name: String): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(name: String, sourceInfo: WSourceInfo): this(name) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue {
        val headSymbol = WSymbol(name, sourceInfo)
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