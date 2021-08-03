package type

import scope.WScope
import source.WSourceInfo
import java.util.*

class WMap(private val map: MutableMap<WValue, WValue>, sourceInfo: WSourceInfo? = null): WValue(sourceInfo) {
    override fun head(): WValue = WNil(sourceInfo)
    override fun tail(): WValue = WNil(sourceInfo)
    override fun eval(scope: WScope) = this

    override fun invoke(scope: WScope, rawArguments: WValue): WValue {
        val key = rawArguments.head().eval(scope)
        return if(rawArguments.tail().truthy()) {
            val value = rawArguments.tail().head().eval(scope)
            map[key] = value
            WNIL
        } else {
            map[key] ?: WNIL
        }
    }

    override fun toString(): String {
        val builder = StringBuilder().append("<Map")
        map.forEach { (key, value) ->
            builder.append(" $key:$value")
        }
        builder.append(" >")
        return builder.toString()
    }

    operator fun get(key: WValue): WValue {
        return map[key] ?: WNil(sourceInfo)
    }

    operator fun set(key: WValue, value: WValue) {
        if(key.isMutable()) {
            throw IllegalArgumentException("Cannot use mutable value as key: $key")
        }
        map[key] = value
    }

    override fun eq(other: WValue): Boolean = other is WMap && map == other.map
    override fun hash() = Objects.hash(map)

    override fun isMutable() = true
}