interface WScope {
    operator fun get(variable: WSymbol): WValue
    operator fun set(variable: WSymbol, value: WValue)
    operator fun contains(variable: WSymbol): Boolean
}