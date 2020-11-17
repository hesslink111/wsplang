data class WProgram(val lists: List<WValue>) {
    fun eval(): WValue {
        val scope = WFunctionScope()
        var retVal: WValue = WNil()
        lists.forEach {
            println(it)
            retVal = it.eval(scope)
            println("=> $retVal")
        }
        return retVal
    }
}