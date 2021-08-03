package scope

import type.WSymbol
import type.WValue

class WScope(val cwd: String, val symbolMap: MutableMap<WSymbol, WValue> = mutableMapOf()) {
    val replacedSymbols: MutableMap<WSymbol, WValue?> = mutableMapOf()

    operator fun get(variable: WSymbol): WValue {
        return symbolMap[variable] ?: throw IllegalArgumentException("Variable $variable not in scope.")
    }

    fun contains(variable: WSymbol): Boolean {
        return variable in symbolMap
    }

    operator fun set(variable: WSymbol, value: WValue) {
        if(variable !in replacedSymbols) {
            replacedSymbols[variable] = symbolMap[variable]
        }
        symbolMap[variable] = value
    }

    inline fun<T> withSubScope(block: (WScope) -> T): T {
        val subScope = WScope(cwd, symbolMap)
        val r = block(subScope)
        subScope.replacedSymbols.forEach { (variable, value) ->
            if(value != null) {
                symbolMap[variable] = value
            } else {
                symbolMap.remove(variable)
            }
        }
        return r
    }
}