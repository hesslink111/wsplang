package type

import scope.WScope
import scope.withFunctionSubScope
import source.WSourceInfo

data class WMacroFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments // Use unevaluated arguments.

        return parentScope.withFunctionSubScope { newScope ->
            while(parameters !is WNil) {
                val argument = args.head()

                val parameter = parameters.head() as? WSymbol
                        ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
                newScope.let(parameter, argument)
                args = args.tail()
                parameters = parameters.tail()
            }

            body.eval(newScope)
        }
    }
    override fun toString() = "<MacroFunction $params $body>"
}