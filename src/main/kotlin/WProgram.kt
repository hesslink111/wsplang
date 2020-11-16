data class WProgram(val lists: List<WValue>) {
    fun eval(): WValue {
        val scope = WScope()
        var retVal: WValue = WNil()
        lists.forEach {
            println("wsp> $it")
            retVal = it.eval(scope)
            println(retVal)
        }
        return retVal
    }
}