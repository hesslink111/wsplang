package type

import scope.WScope
import source.WSourceInfo

data class WNumber(val num: Double): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke number: $num")
    override fun toString(): String = num.toString()
}