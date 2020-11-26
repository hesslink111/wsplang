package type

import scope.WScope
import source.WSourceInfo

data class WList(override val head: WValue, override val tail: WValue): WIList {
    override var sourceInfo: WSourceInfo? = null

    override fun eval(scope: WScope): WValue {
        try {
            val evalHead = head().eval(scope)
            if(evalHead is WMacroFunction) {
                return evalHead.invoke(scope, tail()).eval(scope)
            }
            return evalHead.invoke(scope, tail())
        } catch(ex: Exception) {
            println("Exception occurred while evaluating near '$head' in ${sourceInfo?.filename}:${sourceInfo?.sourceLocation?.line}:${sourceInfo?.sourceLocation?.column}")
            throw ex
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke list: $this")

    override fun toString() = toStringHelper()
}