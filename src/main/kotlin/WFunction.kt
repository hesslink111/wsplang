data class WFunction(val parentScope: WScope, val params: WValue, val body: WValue): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = WNil().also { it.sourceInfo = sourceInfo }

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