data class WScope(private val variables: MutableMap<WSymbol, WValue>, private val parentScope: WScope?) {
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

    fun contains(variable: WSymbol): Boolean {
        return variables.contains(variable) || parentScope?.contains(variable) == true
    }
}