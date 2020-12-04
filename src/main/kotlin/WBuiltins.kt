import scope.withFunctionSubScope
import type.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object WBuiltins {
    private val builtins = listOf(
            WBuiltinFunction("quote") { _, _, rawArguments -> rawArguments.head() },
            WBuiltinFunction("lambda") { self, scope, rawArguments ->
                WFunction(scope, rawArguments.head(), rawArguments.tail().head(), self.sourceInfo)
            },
            WBuiltinFunction("cond") { self, scope, rawArguments ->
                if(rawArguments.falsy()) {
                    return@WBuiltinFunction rawArguments
                } else {
                    val condAction = rawArguments.head()
                    val cond = condAction.head()
                    val action = condAction.tail().head()
                    if(cond.eval(scope).truthy()) {
                        return@WBuiltinFunction action.eval(scope)
                    } else {
                        return@WBuiltinFunction self.onInvoke(self, scope, rawArguments.tail())
                    }
                }
            },

            WBuiltinFunction("list?") { self, scope, rawArguments ->
                WBoolean.from(rawArguments.head().eval(scope).let { it is WIList || it.falsy() }, self.sourceInfo)
            },
            WBuiltinFunction("list") { _, scope, rawArguments ->
                rawArguments.map { it.eval(scope) }
            },
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
                        val b = assignment.tail().head().eval(scope)
                        newScope.let(a as WSymbol, b)
                    }

                    var retVal: WValue = WNil(self.sourceInfo)
                    forms.forEach { form ->
                        retVal = form.eval(newScope)
                    }
                    retVal
                }
            },
            WBuiltinFunction("setq") { _, scope, rawArguments ->
                // (setq x 10)
                val a = rawArguments.head()
                val b = rawArguments.tail().head().eval(scope)
                scope[a as WSymbol] = b
                b
            },
            WBuiltinFunction("cons") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()
                WList(a, b, self.sourceInfo)
            },
            WBuiltinFunction("eq?") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()
                WBoolean.from(a == b, self.sourceInfo)
            },
            WBuiltinFunction("print-raw") { self, scope, rawArguments ->
                print((rawArguments.map { it.eval(scope) }.head() as WString).value)
                return@WBuiltinFunction WNil(self.sourceInfo)
            },
            WBuiltinFunction("print-line") { self, _, _ ->
                println()
                return@WBuiltinFunction WNil(self.sourceInfo)
            },
            WBuiltinFunction("to-string") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                WString(args.head().toString(), self.sourceInfo)
            },
            WBuiltinFunction("begin") { _, scope, rawArguments ->
                var res = rawArguments.map { it.eval(scope) }
                while(res.tail().truthy()) {
                    res = res.tail()
                }
                res.head()
            },
            WBuiltinFunction(">") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a gt b
            },
            WBuiltinFunction("<") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a lt b
            },
            WBuiltinFunction("=") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber

                WBoolean.from(a.num == b.num, self.sourceInfo)
            },
            WBuiltinFunction("or") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()
                val b = args.tail().head()

                WBoolean.from(a.truthy() || b.truthy(), self.sourceInfo)
            },
            WBuiltinFunction("nil?") { self, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head()

                WBoolean.from(a.falsy(), self.sourceInfo)
            },
            WBuiltinFunction("+") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a + b
            },
            WBuiltinFunction("-") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a - b
            },
            WBuiltinFunction("*") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a * b
            },
            WBuiltinFunction("/") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val a = args.head() as WNumber
                val b = args.tail().head() as WNumber
                a / b
            },

            // Map
            WBuiltinFunction("make-map-eval") { self, scope, rawArguments ->
                var args = rawArguments.head().eval(scope)
                val map = mutableMapOf<WValue, WValue>()
                while(args.truthy()) {
                    val pair = args.head()
                    val key = pair.head().eval(scope)
                    val value = pair.tail().head().eval(scope)
                    map[key] = value
                    args = args.tail()
                }
                WMap(map, self.sourceInfo)
            },
            WBuiltinFunction("map?") { self, scope, rawArguments ->
                val a = rawArguments.head().eval(scope)
                WBoolean.from(a is WMap, self.sourceInfo)
            },
            WBuiltinFunction("map-set") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val map = args.head() as WMap
                val key = args.tail().head()
                val value = args.tail().tail().head()
                map[key] = value
                map
            },
            WBuiltinFunction("map-get") { _, scope, rawArguments ->
                val args = rawArguments.map { it.eval(scope) }
                val map = args.head() as WMap
                val key = args.tail().head()
                map[key]
            },

            // Threads
            WBuiltinFunction("thread") { self, scope, rawArguments ->
                val arg = rawArguments.head()
                thread {
                    arg.eval(scope)
                }
                WNil(self.sourceInfo)
            },

            // Exit
            WBuiltinFunction("exit") { _, _, _ ->
                exitProcess(0)
            }
    ).associateBy { it.name }

    operator fun get(name: String): WValue = builtins[name]!!
    operator fun contains(name: String): Boolean = name in builtins
}