package type

import scope.WScope
import source.WSourceInfo
import java.util.*

class WString(val value: String, sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke string: $value.")
    override fun toString(): String = "\"$value\""
    override fun eq(other: WValue) = other is WString && value == other.value
    override fun hash() = Objects.hash(value)
}
