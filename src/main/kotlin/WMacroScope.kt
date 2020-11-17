data class WMacroScope(private val symbolMap: MutableMap<WSymbol, WValue>, private val parentScope: WScope): WScope {
    override operator fun get(variable: WSymbol): WValue {
        return symbolMap[variable] ?: parentScope[variable]
    }

    override operator fun set(variable: WSymbol, value: WValue) {
        // Set in parent scope by default, not in current scope.
        // Never allow a new variable to be declared in the current scope.
        when {
            symbolMap.containsKey(variable) -> symbolMap[variable] = value
            else -> parentScope[variable] = value
        }
    }

    override operator fun contains(variable: WSymbol): Boolean {
        return symbolMap.contains(variable) || parentScope.contains(variable)
    }
}

inline fun<T> WScope.withMacroSubScope(symbols: MutableMap<WSymbol, WValue>, block: (WScope) -> T): T {
    return block(WMacroScope(symbols, this))
}