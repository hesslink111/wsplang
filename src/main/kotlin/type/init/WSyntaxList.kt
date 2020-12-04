package type.init

import scope.WScope
import source.WSourceInfo
import type.*

data class WSyntaxList(override val head: WValue, override val tail: WValue): WIList {
    override lateinit var sourceInfo: WSourceInfo

    constructor(head: WValue, tail: WValue, sourceInfo: WSourceInfo): this(head, tail) {
        this.sourceInfo = sourceInfo
    }

    override fun eval(scope: WScope): WValue {
        try {
            val evalHead = head().eval(scope)
            if(evalHead is WMacroFunction || evalHead is WBuiltinFunction) {
                return evalHead.invoke(scope, tail())
            }
            return WList(evalHead, tail().convertToWList(scope), sourceInfo)
        } catch(ex: Exception) {
            println("Exception occurred while compiling near '$head' in ${sourceInfo.location()}")
            throw ex
        }
    }
    override fun invoke(scope: WScope, rawArguments: WValue) = WList(head(), tail(), sourceInfo)

    override fun toString(): String = "#${toStringHelper()}"
}