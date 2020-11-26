package type

import WScope
import WSourceInfo
import type.init.WSyntaxSymbol
import withMacroSubScope

data class WMacroFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments // Use unevaluated arguments.
        val macroVars = mutableMapOf<WSymbol, WValue>()
        while(parameters !is WNil) {
            val argument = args.head()

            val parameter = parameters.head() as? WSymbol
                    ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
            macroVars[parameter] = argument
            args = args.tail()
            parameters = parameters.tail()
        }

        return parentScope.withMacroSubScope(macroVars) { newScope ->
            body.eval(newScope)
        }
    }
    override fun toString() = "<MacroFunction $params $body>"
}