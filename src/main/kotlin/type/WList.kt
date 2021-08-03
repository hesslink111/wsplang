package type

import scope.WScope
import source.WSourceInfo
import java.util.*

class WList(val head: WValue, val tail: WValue, sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head() = head
    override fun tail() = tail
    override fun eval(scope: WScope): WValue {
        try {
            val evalHead = head().eval(scope)
            return evalHead.invoke(scope, tail())
        } catch(ex: Exception) {
            println("Exception occurred while evaluating near '$head' in ${sourceInfo?.location()}")
            throw ex
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke list: $this")
    override fun toString() = toStringHelper()

    // Ends in types.WNil if list.
    fun allConsedWValues(): List<WValue> {
        val mutableList = mutableListOf<WValue>()
        addConsedWValuesToList(mutableList)
        return mutableList
    }

    private fun addConsedWValuesToList(mutableList: MutableList<WValue>) {
        mutableList.add(head)
        val tl = tail
        if(tl is WList) {
            tl.addConsedWValuesToList(mutableList)
        } else {
            mutableList.add(tail)
        }
    }

    fun toStringHelper(): String {
        val contents = allConsedWValues()
        return if (contents.last().falsy()) {
            "(${contents.dropLast(1).joinToString(" ")})"
        } else {
            contents.joinToString("·")
        }
    }

    override fun eq(other: WValue) = other is WList && head.eq(other.head) && tail.eq(other.tail)
    override fun hash() = Objects.hash(head, tail)
}