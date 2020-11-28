package type

import scope.WScope
import source.WSourceInfo

data class WString(val value: String): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(value: String, sourceInfo: WSourceInfo): this(value) {
        this.sourceInfo = sourceInfo
    }

    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke string: $value")
    override fun toString(): String = "\"$value\""
}