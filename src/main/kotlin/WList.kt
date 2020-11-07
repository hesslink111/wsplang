import org.jparsec.SourceLocation

data class WList(val head: WValue, val tail: WValue): WValue {
    override var sourceLocation: SourceLocation? = null
    override fun head() = head
    override fun tail() = tail

    override fun eval(scope: WScope): WValue {
        try {
            return when((head as? WSymbol)?.name) {
                // Non-evaluated.
                "quote" -> tail().head()
                "lambda" -> tail()
                "cond" -> {
                    // (cond (t (print x)) (t (print y)))
                    var args = tail()
                    var result: WValue = WNil().also { it.sourceLocation = sourceLocation }
                    while(args !is WNil) {
                        val condAction = args.head()
                        val cond = condAction.head()
                        val action = condAction.tail().head()
                        if(cond.eval(scope) !is WNil) {
                            result = action.eval(scope)
                            break
                        }
                        args = args.tail()
                    }

                    result
                }

                // Eval individual args in tail -> tail[0].eval() tail[1].eval() ...
                "list" -> evalArguments(head.sourceLocation, scope)
                "eval" -> evalArguments(head.sourceLocation, scope).head().eval(scope)
                "head" -> evalArguments(head.sourceLocation, scope).head().head()
                "tail" ->  evalArguments(head.sourceLocation, scope).head().tail()
                "set" -> {
                    // (set 'x 10 'y 20)
                    var args = evalArguments(head.sourceLocation, scope)
                    while(args !is WNil) {
                        val symbol = args.head()
                        args = args.tail()
                        val value = args.head()
                        args = args.tail()
                        scope[symbol as WSymbol] = value
                    }
                    WNil().also { it.sourceLocation = sourceLocation }
                }
                "cons" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head()
                    val b = args.tail().head()
                    WList(a, b).also { it.sourceLocation = sourceLocation }
                }
                "eq" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head()
                    val b = args.tail().head()

                    return when {
                        a is WNil && b is WNil -> WBoolean.from(sourceLocation, true)
                        a is WSymbol && b is WSymbol -> WBoolean.from(sourceLocation, a.name == b.name)
                        a is WNumber && b is WNumber -> WBoolean.from(sourceLocation, a.num == b.num)
                        a is WString && b is WString -> WBoolean.from(sourceLocation, a.value == b.value)
                        a is WList && b is WList -> WBoolean.from(sourceLocation, a == b)
                        else -> WNil()
                    }.also { it.sourceLocation = sourceLocation }
                }
                "print-raw" -> {
                    print((evalArguments(head.sourceLocation, scope).head() as WString).value)
                    WNil().also { it.sourceLocation = sourceLocation }
                }
                "print-line" -> {
                    println()
                    WNil().also { it.sourceLocation = sourceLocation }
                }
                "to-string" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    WString(args.head().toString()).also { it.sourceLocation = sourceLocation }
                }
                "progn" -> {
                    var res = evalArguments(head.sourceLocation, scope)
                    while(res.tail() !is WNil) {
                        res = res.tail()
                    }
                    res.head()
                }
                ">" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head() as WNumber
                    val b = args.tail().head() as WNumber

                    return WBoolean.from(sourceLocation, a.num > b.num).also { it.sourceLocation = sourceLocation }
                }
                "<" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head() as WNumber
                    val b = args.tail().head() as WNumber

                    return WBoolean.from(sourceLocation, a.num < b.num).also { it.sourceLocation = sourceLocation }
                }
                "=" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head() as WNumber
                    val b = args.tail().head() as WNumber

                    WBoolean.from(sourceLocation, a.num == b.num).also { it.sourceLocation = sourceLocation }
                }
                "null" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head()

                    WBoolean.from(sourceLocation, a is WNil).also { it.sourceLocation = sourceLocation }
                }
                "+" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head() as WNumber
                    val b = args.tail().head() as WNumber
                    WNumber(a.num + b.num).also { it.sourceLocation = sourceLocation }
                }
                "-" -> {
                    val args = evalArguments(head.sourceLocation, scope)
                    val a = args.head() as WNumber
                    val b = args.tail().head() as WNumber
                    WNumber(a.num - b.num).also { it.sourceLocation = sourceLocation }
                }

                // Eval function
                // Functions: ((x y) (+ x y))
                else -> {
                    val functionContents = head.eval(scope)

                    if(functionContents !is WList) {
                        throw IllegalArgumentException("$head does not evaluate to a function in the current scope: $functionContents")
                    }

                    // Set variables to values specified in saved lambda.
                    var args = evalArguments(sourceLocation, scope)
                    var parameters = functionContents.head()
                    val body = (functionContents.tail()).head()

                    val localVariables = mutableMapOf<WSymbol, WValue>()

                    while(parameters !is WNil) {
                        val argument = args.head()

                        val parameter = parameters.head() as? WSymbol ?: throw IllegalArgumentException("expected parameter, found: ${parameters.head()}")
                        localVariables[parameter] = argument
                        args = args.tail()
                        parameters = parameters.tail()
                    }

                    val functionScope = WScope(localVariables, scope)
                    return body.eval(functionScope)
                }
            }
        } catch(ex: Exception) {
            println("Exception occured while evaluating near ${head}: Line: ${sourceLocation?.line}, Column:${sourceLocation?.column}")
            throw ex
        }
    }

    private fun evalArguments(sl: SourceLocation?, scope: WScope): WValue {
        return if(tail is WList) {
            WList(tail.head().eval(scope), tail.evalArguments(sl, scope))
        } else {
            WNil().also { it.sourceLocation = sl }
        }
    }

    // Ends in WNil if list.
    private fun allConsedWValues(): List<WValue> {
        val mutableList = mutableListOf<WValue>()
        addConsedWValuesToList(mutableList)
        return mutableList
    }

    private fun addConsedWValuesToList(mutableList: MutableList<WValue>) {
        mutableList.add(head)
        if(tail is WList) {
            tail.addConsedWValuesToList(mutableList)
        } else {
            mutableList.add(tail)
        }
    }

    override fun toString(): String {
        val contents = allConsedWValues()
        return if (contents.last() is WNil) {
            "(${contents.dropLast(1).joinToString(" ")})"
        } else {
            contents.joinToString("·")
        }
    }
}