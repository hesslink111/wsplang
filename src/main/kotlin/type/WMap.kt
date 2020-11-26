package type

import WScope
import WSourceInfo

class WMap(private val map: MutableMap<WValue, WValue>): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun tail(): WValue = WNil().also { it.sourceInfo = sourceInfo }
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke map: $this")

    override fun toString(): String {
        val builder = StringBuilder().append("<Map")
        map.forEach { (key, value) ->
            builder.append(" ($key $value)")
        }
        builder.append(">")
        return builder.toString()
    }

    operator fun get(key: WValue): WValue {
        return map[key] ?: WNil().also { it.sourceInfo = sourceInfo }
    }

    operator fun set(key: WValue, value: WValue) {
        map[key] = value
    }
}