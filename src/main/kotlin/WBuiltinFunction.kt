import org.jparsec.SourceLocation

data class WBuiltinFunction(val name: String, val onInvoke: (self: WBuiltinFunction, scope: WScope, rawArguments: WValue) -> WValue): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head(): WValue = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail(): WValue = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope): WValue = WNil().also { it.sourceLocation = sourceLocation }
    override fun invoke(scope: WScope, rawArguments: WValue): WValue = onInvoke(this, scope, rawArguments)
    override fun toString(): String = "<Builtin $name>"
}