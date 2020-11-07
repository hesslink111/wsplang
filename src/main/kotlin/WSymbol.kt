import org.jparsec.SourceLocation

data class WSymbol(val name: String): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail() = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope): WValue {
        return when (name) {
            "t" -> this
            else -> scope[this]
        }
    }

    override fun toString(): String = "$name"
}