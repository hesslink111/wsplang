package type

import WBuiltins
import scope.WScope
import source.WSourceInfo
import java.util.*

class WSymbol(val name: String, sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue {
        return when (name) {
            in WBuiltins -> WBuiltins[name]
            else -> scope[this]
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        throw IllegalArgumentException("Cannot invoke symbol: $name")
    }

    override fun toString(): String = name

    override fun eq(other: WValue) = other is WSymbol && name == other.name
    override fun hash() = Objects.hash(name)
}