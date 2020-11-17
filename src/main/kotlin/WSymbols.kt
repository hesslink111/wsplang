object WSymbols {
    val builtins = listOf(
            WBuiltinFunction("quote") { _, _, rawArguments -> rawArguments.head() },
            WBuiltinFunction("lambda") { self, scope, rawArguments ->
                WFunction(scope, rawArguments.head(), rawArguments.tail().head()).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("cond") { self, scope, rawArguments ->
                if(rawArguments is WNil) {
                    return@WBuiltinFunction rawArguments
                } else {
                    val condAction = rawArguments.head()
                    val cond = condAction.head()
                    val action = condAction.tail().head()
                    if(cond.eval(scope) !is WNil) {
                        return@WBuiltinFunction action.eval(scope)
                    } else {
                        return@WBuiltinFunction self.onInvoke(self, scope, rawArguments.tail())
                    }
                }
            },
            WBuiltinFunction("define") { self, scope, rawArguments ->
                // (define var value)
                // (define (func arg1 arg2 ...) body ...)
                val signature = rawArguments.head()
                if(signature is WList) {
                    val params = signature.tail()
                    val body = WList(WSymbol("begin"), rawArguments.tail()).also { it.sourceInfo = self.sourceInfo }
                    val function = WFunction(scope, params, body)
                    scope[signature.head() as WSymbol] = function
                    return@WBuiltinFunction function
                } else {
                    val value = rawArguments.tail().head().eval(scope)
                    scope[signature as WSymbol] = value
                    return@WBuiltinFunction value
                }
            },
            WBuiltinFunction("if") { self, scope, rawArguments ->
                val condition = rawArguments.head()
                val success = rawArguments.tail().head()
                val failure = rawArguments.tail().tail().head()

                if(condition.eval(scope).truthy()) success.eval(scope) else failure.eval(scope)
            },

            WBuiltinFunction("list") { _, scope, rawArguments -> rawArguments.map { it.eval(scope) } },
            WBuiltinFunction("eval") { _, scope, rawArguments ->
                rawArguments.map { it.eval(scope) }.head().eval(scope)
            },
            WBuiltinFunction("head") { _, scope, rawArguments ->
                rawArguments.map { it.eval(scope) }.head().head()
            },
            WBuiltinFunction("tail") { _, scope, rawArguments ->
                rawArguments.map { it.eval(scope) }.head().tail()
            },
            WBuiltinFunction("let") { self, scope, rawArguments ->
                val assignments = rawArguments.head()
                val forms = rawArguments.tail()

                scope.withNewScope { newScope ->
                    assignments.forEach { assignment ->
                        val a = assignment.head()
                        val b = assignment.tail().head()
                        newScope[a as WSymbol] = b
                    }

                    var retVal: WValue = WNil().also { it.sourceInfo = self.sourceInfo }
                    forms.forEach { form ->
                        retVal = form.eval(newScope)
                    }
                    retVal
                }
            },
            WBuiltinFunction("set") { self, scope, rawArguments ->
                // (set 'x 10)
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()
                scope[a as WSymbol] = b
                b
            },
            WBuiltinFunction("cons") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()
                WList(a, b).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("eq") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()

                when {
                    a is WNil && b is WNil -> WBoolean.from(self.sourceInfo, true)
                    a is WSymbol && b is WSymbol -> WBoolean.from(self.sourceInfo, a.name == b.name)
                    a is WNumber && b is WNumber -> WBoolean.from(self.sourceInfo, a.num == b.num)
                    a is WString && b is WString -> WBoolean.from(self.sourceInfo, a.value == b.value)
                    a is WList && b is WList -> WBoolean.from(self.sourceInfo, a == b)
                    else -> WNil().also { it.sourceInfo = self.sourceInfo }
                }
            },
            WBuiltinFunction("print-raw") { self, scope, rawArguments ->
                print((rawArguments.map { it.eval(scope) }.head() as WString).value)
                WNil().also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("print-line") { self, _, _ ->
                println()
                WNil().also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("to-string") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                WString(args.head().toString()).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("begin") { _, scope, rawArguments ->
                var res = rawArguments.map { it.eval(scope) }
                while(res.tail() !is WNil) {
                    res = res.tail()
                }
                res.head()
            },
            WBuiltinFunction(">") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber

                WBoolean.from(self.sourceInfo, a.num > b.num)
            },
            WBuiltinFunction("<") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber

                WBoolean.from(self.sourceInfo, a.num < b.num)
            },
            WBuiltinFunction("=") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber

                WBoolean.from(self.sourceInfo, a.num == b.num)
            },
            WBuiltinFunction("or") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()

                WBoolean.from(self.sourceInfo, a !is WNil || b !is WNil)
            },
            WBuiltinFunction("null") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()

                WBoolean.from(self.sourceInfo, a is WNil)
            },
            WBuiltinFunction("+") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                WNumber(a.num + b.num).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("-") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                WNumber(a.num - b.num).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("*") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                WNumber(a.num * b.num).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("/") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                WNumber(a.num / b.num).also { it.sourceInfo = self.sourceInfo }
            },
    ).associateBy { it.name }
}