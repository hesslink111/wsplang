import org.jparsec.SourceLocation

data class WNil(val unit: Unit = Unit): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = this
    override fun tail() = this
    override fun eval(scope: WScope) = this
    override fun toString(): String = "nil"
}