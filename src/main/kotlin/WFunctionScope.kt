data class WFunctionScope(private val symbolMap: MutableMap<WSymbol, WValue>, private val parentScope: WScope?): WScope {
    constructor(): this(mutableMapOf(), null)

    override operator fun get(variable: WSymbol): WValue {
        return symbolMap[variable] ?: parentScope?.get(variable) ?: WNil()
    }

    override operator fun set(variable: WSymbol, value: WValue) = when {
        symbolMap.containsKey(variable) -> symbolMap[variable] = value
        parentScope?.contains(variable) == true -> parentScope[variable] = value
        else -> symbolMap[variable] = value
    }

    override fun contains(variable: WSymbol): Boolean {
        return symbolMap.contains(variable) || parentScope?.contains(variable) == true
    }
}

inline fun<T> WScope.withFunctionSubScope(block: (WScope) -> T): T {
    return block(WFunctionScope(mutableMapOf(), this))
}