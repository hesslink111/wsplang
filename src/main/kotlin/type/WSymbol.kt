package type

import WBuiltins
import scope.WScope
import source.WSourceInfo

data class WSymbol(val name: String): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(name: String, sourceInfo: WSourceInfo): this(name) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue {
        return when (name) {
            "t" -> this
            in WBuiltins -> WBuiltins[name]
            else -> scope[this]
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        throw IllegalArgumentException("Cannot invoke symbol: $name")
    }

    override fun toString(): String = name
}