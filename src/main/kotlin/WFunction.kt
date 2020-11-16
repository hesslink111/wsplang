import org.jparsec.SourceLocation

data class WFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail() = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope) = WNil().also { it.sourceLocation = sourceLocation }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        var parameters = params
        var args = rawArguments.map { it.eval(scope) }
        return parentScope.withNewScope { newScope ->
            while(parameters !is WNil) {
                val argument = args.head()

                val parameter = parameters.head() as? WSymbol
                        ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
                newScope[parameter] = argument
                args = args.tail()
                parameters = parameters.tail()
            }
            return@withNewScope body.eval(newScope)
        }
    }
    override fun toString() = "<Function $params $body>"
}