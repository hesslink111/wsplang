import scope.WScope
import type.WNil
import type.WValue

data class WCompiledCode(val lists: List<WValue>) {
    fun eval(scope: WScope): WValue {
        var retVal: WValue = WNil()
        lists.forEach {
            println(it)
            retVal = it.eval(scope)
            println("=> $retVal")
        }
        return retVal
    }
}