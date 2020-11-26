package type

import scope.WScope
import source.WSourceInfo
import scope.withFunctionSubScope

data class WFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments.map { it.eval(scope) }
        return parentScope.withFunctionSubScope { newScope ->
            while(parameters !is WNil) {
                val argument = args.head()

                val parameter = parameters.head() as? WSymbol
                        ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
                newScope.let(parameter, argument)
                args = args.tail()
                parameters = parameters.tail()
            }
            return@withFunctionSubScope body.eval(newScope)
        }
    }
    override fun toString() = "<Function $params $body>"
}