package type

import source.WSourceInfo
import scope.WScope
import java.util.*

class WFunction(
    private val parentScope: WScope,
    val params: WValue,
    val body: WValue,
    sourceInfo: WSourceInfo? = null
): WValue(sourceInfo) {
    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments.map { it.eval(scope) }
        return parentScope.withSubScope { newScope ->
            while(parameters.truthy()) {
                val argument = args.head()

                val parameter = parameters.head() as? WSymbol
                        ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
                newScope[parameter] = argument
                args = args.tail()
                parameters = parameters.tail()
            }
            return@withSubScope body.eval(newScope)
        }
    }
    override fun toString() = "<Function $params $body>"
    override fun eq(other: WValue) = other is WFunction && parentScope === other.parentScope && params.eq(other.params) && body.eq(other.body)
    override fun hash() = Objects.hash(parentScope, params, body)
}