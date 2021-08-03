import scope.WScope
import source.WProgramParser
import type.*
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object WBuiltins {
    private val builtins = listOf(
            WBuiltinFunction("debug_print_scope") { _, scope, _ ->
                println("Scope - symbols: ")
                scope.symbolMap.forEach { (k, v) ->
                    println("K: $k, V: $v")
                }
                println("Scope - replacedSymbols: ")
                scope.replacedSymbols.forEach { (k, v) ->
                    println("K: $k, V: $v")
                }
                WNIL
            },

            WBuiltinFunction("load") { self, scope, rawArguments ->
                val fileName = rawArguments.head() as WString
                val file = File(scope.cwd, fileName.value).absoluteFile

                // Build function which returns exported symbols.
                val loadScope = WScope(file.parent)
                WProgramParser(file.absolutePath)
                    .parseAsProgram(file.readText())
                    .eval(loadScope)

                // Put loadScope into WMap.
                val map: MutableMap<WValue, WValue> = mutableMapOf()
                map.putAll(loadScope.symbolMap)
                WMap(map, self.sourceInfo)
            },
            WBuiltinFunction("define") { self, scope, rawArguments ->
                val signature = rawArguments.head()
                val body = rawArguments.tail().head()
                return@WBuiltinFunction if(signature is WList) {
                    val name = signature.head() as WSymbol
                    val params = signature.tail()
                    scope[name] = WFunction(scope, params, body, self.sourceInfo)
                    name
                } else {
                    val name = signature as WSymbol
                    scope[name] = body.eval(scope)
                    name
                }
            },

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
                WBoolean.from(rawArguments.head().eval(scope).let { it is WList || it.falsy() }, self.sourceInfo)
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

                scope.withSubScope { newScope ->
                    assignments.forEach { assignment ->
                        val a = assignment.head()
                        val b = assignment.tail().head().eval(scope)
                        newScope[a as WSymbol] = b
                    }

                    var retVal: WValue = WNil(self.sourceInfo)
                    forms.forEach { form ->
                        retVal = form.eval(newScope)
                    }
                    retVal
                }
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
                WBoolean.from(a.eq(b), self.sourceInfo)
            },
            WBuiltinFunction("println") { self, scope, rawArguments ->
                println((rawArguments.head().eval(scope)))
                return@WBuiltinFunction WNil(self.sourceInfo)
            },
            WBuiltinFunction("to_string") { self, scope, rawArguments ->
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
            WBuiltinFunction("make_map") { self, scope, rawArguments ->
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

            // Exit
            WBuiltinFunction("exit") { _, _, _ ->
                exitProcess(0)
            }
    ).associateBy { it.name }

    operator fun get(name: String): WValue = builtins[name]!!
    operator fun contains(name: String): Boolean = name in builtins
}