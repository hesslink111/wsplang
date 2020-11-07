import org.jparsec.SourceLocation

data class WNumber(val num: Double): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail() = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope) = this
    override fun toString(): String = num.toString()
}