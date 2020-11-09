import org.jparsec.SourceLocation
import java.lang.IllegalArgumentException

data class WNumber(val num: Double): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = WNil().also { it.sourceLocation = sourceLocation }
    override fun tail() = WNil().also { it.sourceLocation = sourceLocation }
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke number: $num")
    override fun toString(): String = num.toString()
}