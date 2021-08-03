package type

import scope.WScope
import source.WBuiltinSourceInfo
import source.WSourceInfo
import java.util.*

private typealias OnInvoke = (self: WBuiltinFunction, scope: WScope, rawArguments: WValue) -> WValue

data class WBuiltinFunction(val name: String, val onInvoke: OnInvoke): WValue(WBuiltinSourceInfo(name)) {
    override fun head(): WValue = WNil(sourceInfo)
    override fun tail(): WValue = WNil(sourceInfo)
    override fun eval(scope: WScope): WValue = this
    override fun invoke(scope: WScope, rawArguments: WValue): WValue = onInvoke(this, scope, rawArguments)
    override fun toString(): String = "<Builtin $name>"
    override fun eq(other: WValue) = other is WBuiltinFunction && this.name == other.name
    override fun hash() = Objects.hash(name)
}