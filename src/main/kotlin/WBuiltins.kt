import java.io.File
import java.nio.file.Path

object WBuiltins {
    private val builtins = listOf(
            WBuiltinFunction("quote") { _, _, rawArguments -> rawArguments.head() },
            WBuiltinFunction("macro") { self, scope, rawArguments ->
                WMacroFunction(scope, rawArguments.head(), rawArguments.tail().head())
                        .also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("lambda") { self, scope, rawArguments ->
                WFunction(scope, rawArguments.head(), rawArguments.tail().head())
                        .also { it.sourceInfo = self.sourceInfo }
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

            WBuiltinFunction("load") { self, scope, rawArguments ->
                // TODO: Make this happen at compile time.
                val filename = rawArguments.head().eval(scope) as WString
                val filePath = Path.of(filename.value)
                val file = if(filePath.isAbsolute) {
                    filePath.toFile()
                } else {
                    Path.of(rawArguments.sourceInfo!!.filename).resolveSibling(filePath).toFile()
                }
                WProgramParser(file).parse().eval(scope)
            },
            WBuiltinFunction("listp") { self, scope, rawArguments ->
                WBoolean.from(self.sourceInfo, rawArguments.head().eval(scope).let { it is WList || it is WNil })
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

                scope.withFunctionSubScope { newScope ->
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
                WBoolean.from(self.sourceInfo, a == b)
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

            // Map
            WBuiltinFunction("make-hash") { self, scope, rawArguments ->
                var args = rawArguments.head()
                val map = mutableMapOf<WValue, WValue>()
                while(args.truthy()) {
                    val pair = args.head()
                    val key = pair.head().eval(scope)
                    val value = pair.tail().head().eval(scope)
                    map[key] = value
                    args = args.tail()
                }
                WMap(map).also { it.sourceInfo = self.sourceInfo }
            },
            WBuiltinFunction("hash-set") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val map = args.head() as WMap
                val key = args.tail().head()
                val value = args.tail().tail().head()
                map[key] = value
                map
            },
            WBuiltinFunction("hash-get") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val map = args.head() as WMap
                val key = args.tail().head()
                map[key]
            },
    ).associateBy { it.name }

    operator fun get(name: String): WValue = builtins[name]!!
    operator fun contains(name: String): Boolean = name in builtins
}