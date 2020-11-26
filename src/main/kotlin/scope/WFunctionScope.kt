package scope

import type.WNil
import type.WSymbol
import type.WValue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

data class WFunctionScope(private val symbolMap: MutableMap<WSymbol, WValue>, private val parentScope: WScope?, override val lock: Lock): WScope {
    constructor(): this(mutableMapOf(), null, ReentrantLock())

    override fun getInternal(variable: WSymbol): WValue {
        return symbolMap[variable] ?: parentScope?.getInternal(variable) ?: WNil()
    }

    override fun setInternal(variable: WSymbol, value: WValue) {
        if(setIfContains(variable, value)) {
            return
        } else {
            symbolMap[variable] = value
        }
    }

    override fun setIfContains(variable: WSymbol, value: WValue): Boolean {
        return if(variable in symbolMap) {
            symbolMap[variable] = value
            true
        } else {
            parentScope?.setIfContains(variable, value) ?: false
        }
    }

    override fun containsInternal(variable: WSymbol): Boolean {
        return variable in symbolMap || parentScope?.containsInternal(variable) == true
    }

    override fun letInternal(variable: WSymbol, value: WValue) {
        symbolMap[variable] = value
    }
}

inline fun<T> WScope.withFunctionSubScope(block: (WScope) -> T): T {
    return block(WFunctionScope(mutableMapOf(), this, this.lock))
}