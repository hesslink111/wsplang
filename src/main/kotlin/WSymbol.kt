data class WSymbol(val name: String): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope): WValue {
        return when (name) {
            "t" -> this
            in WBuiltins -> WBuiltins[name]
            else -> scope[this]
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke symbol: $name")

    override fun toString(): String = name
}