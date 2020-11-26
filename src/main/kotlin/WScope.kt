import type.WSymbol
import type.WValue
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

interface WScope {
    val lock: Lock
    fun getInternal(variable: WSymbol): WValue
    fun setInternal(variable: WSymbol, value: WValue)
    fun setIfContains(variable: WSymbol, value: WValue): Boolean
    fun containsInternal(variable: WSymbol): Boolean

    operator fun get(variable: WSymbol): WValue = lock.withLock { getInternal(variable) }
    operator fun set(variable: WSymbol, value: WValue) = lock.withLock { setInternal(variable, value) }
    operator fun contains(variable: WSymbol): Boolean = lock.withLock { containsInternal(variable) }
}