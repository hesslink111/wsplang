package type

import WScope
import WSourceInfo

data class WInitFunction(val name: String, val onInvoke: (self: WInitFunction, scope: WScope, rawArguments: WValue) -> WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun invoke(scope: WScope, rawArguments: WValue): WValue = throw IllegalArgumentException("Cannot invoke InitFunction: $this")
    override fun toString(): String = "<Init $name>"
}