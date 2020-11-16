data class WScope(private val variables: MutableMap<WSymbol, WValue>, private val parentScope: WScope?) {
    constructor(): this(mutableMapOf(), null)

    operator fun get(variable: WSymbol): WValue {
        return variables[variable] ?: parentScope?.get(variable) ?: WNil()
    }

    operator fun set(variable: WSymbol, value: WValue) {
        when {
            variables.containsKey(variable) -> variables[variable] = value
            parentScope?.contains(variable) == true -> parentScope[variable] = value
            else -> variables[variable] = value
        }
    }

    private fun contains(variable: WSymbol): Boolean {
        return variables.contains(variable) || parentScope?.contains(variable) == true
    }

    inline fun<T> withNewScope(block: (WScope) -> T): T {
        return block(WScope(mutableMapOf(), this))
    }
}