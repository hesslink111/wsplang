package type

import scope.WScope
import source.WSourceInfo

data class WList(override val head: WValue, override val tail: WValue): WIList {
    override lateinit var sourceInfo: WSourceInfo

    constructor(head: WValue, tail: WValue, sourceInfo: WSourceInfo): this(head, tail) {
        this.sourceInfo = sourceInfo
    }

    override fun eval(scope: WScope): WValue {
        try {
            val evalHead = head().eval(scope)
            if(evalHead is WMacroFunction) {
                return evalHead.invoke(scope, tail()).eval(scope)
            }
            return evalHead.invoke(scope, tail())
        } catch(ex: Exception) {
            println("Exception occurred while evaluating near '$head' in ${sourceInfo.location()}")
            throw ex
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke list: $this")

    override fun toString() = toStringHelper()
}