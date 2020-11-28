package type.init

import WInitBuiltins
import scope.WScope
import source.WSourceInfo
import type.*

data class WSyntaxSymbol(val name: String): WSymbol {
    override lateinit var sourceInfo: WSourceInfo

    constructor(name: String, sourceInfo: WSourceInfo): this(name) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue {
        return when {
            name in WInitBuiltins -> WInitBuiltins[name]
            this in scope -> scope[this]
            else -> WRuntimeSymbol(name, sourceInfo)
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        throw IllegalArgumentException("Cannot invoke syntax symbol: $name")
    }

    override fun toString(): String = "#$name"
}