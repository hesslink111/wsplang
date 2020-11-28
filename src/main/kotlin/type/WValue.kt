package type

import scope.WScope
import source.WSourceInfo
import type.init.WSyntaxList

interface WValue {
    val sourceInfo: WSourceInfo
    fun head(): WValue
    fun tail(): WValue
    fun eval(scope: WScope): WValue
    fun invoke(scope: WScope, rawArguments: WValue): WValue
}

fun WValue.map(f: (WValue) -> WValue): WValue = if(this.falsy()) {
    this
} else {
    WList(f(head()), tail().map(f), sourceInfo)
}

fun WValue.mapToSyntaxList(f: (WValue) -> WValue): WValue {
    val list = this.map(f)
    if(list.falsy()) {
        return list
    }
    return WSyntaxList(list.head(), list.tail(), sourceInfo)
}

fun WValue.forEach(f: (WValue) -> Unit) {
    if(this.truthy()) {
        f(head())
        tail().forEach(f)
    }
}

inline fun WValue.truthy(): Boolean = this !is WNil
inline fun WValue.falsy(): Boolean = this is WNil