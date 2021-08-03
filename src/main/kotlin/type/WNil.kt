package type

import scope.WScope
import source.WSourceInfo

class WNil(sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head() = this
    override fun tail() = this
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke nil.")
    override fun toString(): String = "nil"
    override fun eq(other: WValue): Boolean = other is WNil
    override fun hash() = 0
}

val WNIL = WNil()
