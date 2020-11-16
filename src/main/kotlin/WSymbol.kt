import org.jparsec.SourceLocation

data class WSymbol(val name: String): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail() = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope): WValue {
        return when (name) {
            "t" -> this
            in WSymbols.builtins -> WSymbols.builtins[name]!!
            else -> scope[this]
        }
    }

    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke symbol: $name")

    override fun toString(): String = "$name"
}