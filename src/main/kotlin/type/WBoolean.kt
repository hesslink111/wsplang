package type

import scope.WScope
import source.WSourceInfo

object WBoolean {
    fun from(boolean: Boolean, sourceInfo: WSourceInfo?) = if(boolean) {
        WTrue(sourceInfo)
    } else {
        WNil(sourceInfo)
    }
}

class WTrue(sourceInfo: WSourceInfo? = null) : WValue(sourceInfo) {
    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke boolean.")
    override fun toString(): String = "t"
    override fun eq(other: WValue) = other is WTrue
    override fun hash() = 1
}