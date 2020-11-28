package type

import source.WSourceInfo

object WBoolean {
    fun from(boolean: Boolean, sourceInfo: WSourceInfo) = if(boolean) {
        WSymbol("t", sourceInfo)
    } else {
        WNil(sourceInfo)
    }
}
