package type

import source.WSourceInfo

object WBoolean {
    fun from(boolean: Boolean, sourceInfo: WSourceInfo) = if(boolean) {
        WRuntimeSymbol("t", sourceInfo)
    } else {
        WNil(sourceInfo)
    }
}
