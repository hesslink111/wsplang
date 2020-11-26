package type

import scope.WScope
import source.WSourceInfo

data class WBuiltinFunction(val name: String, val onInvoke: (self: WBuiltinFunction, scope: WScope, rawArguments: WValue) -> WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope): WValue = this
    override fun invoke(scope: WScope, rawArguments: WValue): WValue = onInvoke(this, scope, rawArguments)
    override fun toString(): String = "<Builtin $name>"
}