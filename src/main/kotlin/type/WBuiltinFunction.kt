package type

import scope.WScope
import source.WBuiltinSourceInfo
import source.WSourceInfo

private typealias OnInvoke = (self: WBuiltinFunction, scope: WScope, rawArguments: WValue) -> WValue

data class WBuiltinFunction(val name: String, val onInvoke: OnInvoke): WValue {
    override val sourceInfo: WSourceInfo = WBuiltinSourceInfo(name)

    override fun head(): WValue = WNil(sourceInfo)
    override fun tail(): WValue = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue = this
    override fun invoke(scope: WScope, rawArguments: WValue): WValue = onInvoke(this, scope, rawArguments)
    override fun toString(): String = "<Builtin $name>"
}