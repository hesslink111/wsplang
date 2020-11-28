package type

import scope.WScope
import source.WSourceInfo

class WMap(private val map: MutableMap<WValue, WValue>): WValue {
    override lateinit var sourceInfo: WSourceInfo

    constructor(map: MutableMap<WValue, WValue>, sourceInfo: WSourceInfo): this(map) {
        this.sourceInfo = sourceInfo
    }

    override fun head(): WValue = WNil(sourceInfo)
    override fun tail(): WValue = WNil(sourceInfo)
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
        return map[key] ?: WNil(sourceInfo)
    }

    operator fun set(key: WValue, value: WValue) {
        map[key] = value
    }
}