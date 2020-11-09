import org.jparsec.SourceLocation

data class WList(val head: WValue, val tail: WValue): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = head
    override fun tail() = tail

    override fun eval(scope: WScope): WValue {
        try {
            return head().eval(scope).invoke(scope, tail())
        } catch(ex: Exception) {
            println("Exception occured while evaluating near ${head}: Line: ${sourceLocation?.line}, Column:${sourceLocation?.column}")
            throw ex
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        // Set variables to values specified in saved lambda.
        var args = rawArguments.map { it.eval(scope) }
        var parameters = head()
        val body = tail().head()

        val localVariables = mutableMapOf<WSymbol, WValue>()

        while(parameters !is WNil) {
            val argument = args.head()

            val parameter = parameters.head() as? WSymbol
                    ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
            localVariables[parameter] = argument
            args = args.tail()
            parameters = parameters.tail()
        }

        val functionScope = WScope(localVariables, scope)
        return body.eval(functionScope)
    }

    // Ends in WNil if list.
    private fun allConsedWValues(): List<WValue> {
        val mutableList = mutableListOf<WValue>()
        addConsedWValuesToList(mutableList)
        return mutableList
    }

    private fun addConsedWValuesToList(mutableList: MutableList<WValue>) {
        mutableList.add(head)
        if(tail is WList) {
            tail.addConsedWValuesToList(mutableList)
        } else {
            mutableList.add(tail)
        }
    }

    override fun toString(): String {
        val contents = allConsedWValues()
        return if (contents.last() is WNil) {
            "(${contents.dropLast(1).joinToString(" ")})"
        } else {
            contents.joinToString("·")
        }
    }
}