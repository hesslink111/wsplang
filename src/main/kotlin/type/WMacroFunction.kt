package type

import scope.WScope
import scope.withFunctionSubScope
import source.WSourceInfo

data class WMacroFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(parentScope: WScope, params: WValue, body: WValue, sourceInfo: WSourceInfo): this(parentScope, params, body) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments // Use unevaluated arguments.

        return parentScope.withFunctionSubScope { newScope ->
            while(parameters.truthy()) {
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