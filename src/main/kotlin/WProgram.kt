data class WProgram(val lists: List<WValue>) {
    fun eval(scope: WScope = WFunctionScope()): WValue {
        var retVal: WValue = WNil()
        lists.forEach {
            println(it)
            retVal = it.eval(scope)
            println("=> $retVal")
        }
        return retVal
    }
}