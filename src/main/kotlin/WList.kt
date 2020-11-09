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

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke list: $this")

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