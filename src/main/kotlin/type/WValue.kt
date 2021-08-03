package type

import scope.WScope
import source.WSourceInfo
import java.util.*

abstract class WValue(val sourceInfo: WSourceInfo?) {
    abstract fun head(): WValue
    abstract fun tail(): WValue
    abstract fun eval(scope: WScope): WValue
    abstract fun invoke(scope: WScope, rawArguments: WValue): WValue

    fun map(f: (WValue) -> WValue): WValue = if(this.falsy()) {
        this
    } else {
        WList(f(head()), tail().map(f), sourceInfo)
    }

    fun forEach(f: (WValue) -> Unit) {
        if(this.truthy()) {
            f(head())
            tail().forEach(f)
        }
    }

    inline fun truthy(): Boolean = this !is WNil
    inline fun falsy(): Boolean = this is WNil

    // Force implementers to override.
    abstract fun eq(other: WValue): Boolean
    abstract fun hash(): Int

    override fun equals(other: Any?) = other is WValue && eq(other)
    override fun hashCode() = hash()

    open fun isMutable(): Boolean = false
}
