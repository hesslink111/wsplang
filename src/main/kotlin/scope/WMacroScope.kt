package scope

import type.WSymbol
import type.WValue
import java.util.concurrent.locks.Lock

data class WMacroScope(private val symbolMap: MutableMap<WSymbol, WValue>, private val parentScope: WScope, override val lock: Lock): WScope {
    override fun getInternal(variable: WSymbol): WValue {
        return symbolMap[variable] ?: parentScope[variable]
    }

    override fun setInternal(variable: WSymbol, value: WValue) {
        // Set in parent scope by default, not in current scope.
        // Never allow a new variable to be declared in the current scope.
        when (variable) {
            in symbolMap -> symbolMap[variable] = value
            else -> parentScope.setInternal(variable, value)
        }
    }

    override fun setIfContains(variable: WSymbol, value: WValue): Boolean {
        return if(variable in symbolMap) {
            symbolMap[variable] = value
            true
        } else {
            parentScope.setIfContains(variable, value)
        }
    }

    override fun containsInternal(variable: WSymbol): Boolean {
        return variable in symbolMap || parentScope.containsInternal(variable)
    }

    override fun letInternal(variable: WSymbol, value: WValue) {
        parentScope.let(variable, value)
    }
}

inline fun<T> WScope.withMacroSubScope(symbols: MutableMap<WSymbol, WValue>, block: (WScope) -> T): T {
    return block(WMacroScope(symbols, this, lock))
}