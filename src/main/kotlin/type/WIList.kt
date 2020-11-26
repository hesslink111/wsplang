package type

import WScope
import WSourceInfo

interface WIList: WValue {
    val head: WValue
    val tail: WValue

    override var sourceInfo: WSourceInfo?
    override fun head() = head
    override fun tail() = tail

    // Ends in types.WNil if list.
    fun allConsedWValues(): List<WValue> {
        val mutableList = mutableListOf<WValue>()
        addConsedWValuesToList(mutableList)
        return mutableList
    }

    private fun addConsedWValuesToList(mutableList: MutableList<WValue>) {
        mutableList.add(head)
        val tl = tail
        if(tl is WIList) {
            tl.addConsedWValuesToList(mutableList)
        } else {
            mutableList.add(tail)
        }
    }

    fun toStringHelper(): String {
        val contents = allConsedWValues()
        return if (contents.last() is WNil) {
            "(${contents.dropLast(1).joinToString(" ")})"
        } else {
            contents.joinToString("·")
        }
    }
}