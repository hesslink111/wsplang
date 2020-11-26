package type

import scope.WScope
import source.WSourceInfo
import type.init.WSyntaxList

interface WValue {
    var sourceInfo: WSourceInfo?
    fun head(): WValue
    fun tail(): WValue
    fun eval(scope: WScope): WValue
    fun invoke(scope: WScope, rawArguments: WValue): WValue
}

fun WValue.map(f: (WValue) -> WValue): WValue = if(this is WNil) {
    this
} else {
    WList(f(head()), tail().map(f)).also { it.sourceInfo = this.sourceInfo }
}.also { it.sourceInfo = sourceInfo }

fun WValue.mapToSyntaxList(f: (WValue) -> WValue): WValue {
    val list = this.map(f)
    if(list is WNil) {
        return list
    }
    return WSyntaxList(list.head(), list.tail())
}

fun WValue.forEach(f: (WValue) -> Unit): Unit {
    if(this !is WNil) {
        f(head())
        tail().forEach(f)
    }
}

fun WValue.truthy(): Boolean = this !is WNil