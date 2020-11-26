package type.init

import WScope
import WSourceInfo
import type.*

data class WSyntaxList(override val head: WValue, override val tail: WValue): WIList {
    override var sourceInfo: WSourceInfo? = null
    override fun eval(scope: WScope): WValue {
        try {
            val evalHead = head().eval(scope)
            if(evalHead is WMacroFunction || evalHead is WBuiltinFunction) {
                return evalHead.invoke(scope, convertTailToWList(scope))
            }
            return WList(evalHead, convertTailToWList(scope)).also { it.sourceInfo = sourceInfo }
        } catch(ex: Exception) {
            println("Exception occurred while compiling near '$head' in ${sourceInfo?.filename}:${sourceInfo?.sourceLocation?.line}:${sourceInfo?.sourceLocation?.column}")
            throw ex
        }
    }
    override fun invoke(scope: WScope, rawArguments: WValue) = WList(head(), tail())
            .also { it.sourceInfo = sourceInfo }

    private fun convertTailToWList(scope: WScope): WValue {
        return if(tail is WNil) {
            tail
        } else {
            WList(tail.head().eval(scope), (tail as WSyntaxList).convertTailToWList(scope))
                    .also { it.sourceInfo = tail.sourceInfo }
        }
    }

    override fun toString(): String = "#${toStringHelper()}"
}