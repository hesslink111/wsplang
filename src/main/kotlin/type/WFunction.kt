package type

import scope.WScope
import source.WSourceInfo
import scope.withFunctionSubScope

data class WFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(parentScope: WScope, params: WValue, body: WValue, sourceInfo: WSourceInfo): this(parentScope, params, body) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments.map { it.eval(scope) }
        return parentScope.withFunctionSubScope { newScope ->
            while(parameters.truthy()) {
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