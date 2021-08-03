package type

import scope.WScope
import source.WSourceInfo
import java.util.*

class WNumber(val num: Number, sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head() = WNil(sourceInfo)
    override fun tail() = WNil(sourceInfo)
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke number: $num")
    override fun toString(): String = num.toString()
    override fun eq(other: WValue) = other is WNumber && num == other.num
    override fun hash() = Objects.hash(num)
}

inline fun<T> WNumber.operate(that: WNumber, onDoubles: (Double, Double) -> T, onInts: (Int, Int) -> T): T {
    return when {
        this.num is Double || that.num is Double -> onDoubles(this.num.toDouble(), that.num.toDouble())
        else -> onInts(this.num.toInt(), that.num.toInt())
    }
}

operator fun WNumber.plus(that: WNumber): WNumber = WNumber(operate(that, Double::plus, Int::plus), sourceInfo)
operator fun WNumber.minus(that: WNumber): WNumber = WNumber(operate(that, Double::minus, Int::minus), sourceInfo)
operator fun WNumber.times(that: WNumber): WNumber = WNumber(operate(that, Double::times, Int::times), sourceInfo)
operator fun WNumber.div(that: WNumber): WNumber = WNumber(operate(that, Double::div, Int::div), sourceInfo)
infix fun WNumber.gt(that: WNumber): WValue = WBoolean.from(operate(that, { a, b -> a > b }, { a, b -> a > b }), sourceInfo)
infix fun WNumber.lt(that: WNumber): WValue = WBoolean.from(operate(that, { a, b -> a < b }, { a, b -> a < b }), sourceInfo)
