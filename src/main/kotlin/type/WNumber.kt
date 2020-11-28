package type

import scope.WScope
import source.WSourceInfo

data class WNumber(val num: Number): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail() = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke number: $num")
    override fun toString(): String = num.toString()
}

inline fun<T> Number.operate(that: Number, onDoubles: (Double, Double) -> T, onInts: (Int, Int) -> T): T {
    return when {
        this is Double || that is Double -> onDoubles(this.toDouble(), that.toDouble())
        else -> onInts(this.toInt(), that.toInt())
    }
}

operator fun Number.plus(that: Number): Number = operate(that, Double::plus, Int::plus)
operator fun Number.minus(that: Number): Number = operate(that, Double::minus, Int::minus)
operator fun Number.times(that: Number): Number = operate(that, Double::times, Int::times)
operator fun Number.div(that: Number): Number = operate(that, Double::div, Int::div)
infix fun Number.gt(that: Number): Boolean = operate(that, { a, b -> a > b }, { a, b -> a > b })
infix fun Number.lt(that: Number): Boolean = operate(that, { a, b -> a < b }, { a, b -> a < b })
