import source.WFileSourceInfo
import source.WProgramParser
import type.*
import type.init.WSyntaxList
import type.init.WSyntaxSymbol
import java.nio.file.Path

object WInitBuiltins {
    private val builtins = listOf(
            WBuiltinFunction("defmacro") { self, scope, rawArguments ->
                val signature = rawArguments.head()
                val name = signature.head() as WSymbol
                val parameters = signature.tail()
                val body = rawArguments.tail().head()
                val macro = WMacroFunction(scope, parameters, body, self.sourceInfo)
                scope[name] = macro
                macro
            },
            WBuiltinFunction("load") { self, scope, rawArguments ->
                val filename = rawArguments.head().eval(scope) as WString
                val filePath = Path.of(filename.value)
                val file = if(filePath.isAbsolute) {
                    filePath.toFile()
                } else {
                    Path.of((rawArguments.sourceInfo as WFileSourceInfo).filename).resolveSibling(filePath).toFile()
                }
                val syntax = WProgramParser(file).parse().asSyntax()
                WSyntaxList(WSyntaxSymbol("begin", self.sourceInfo), syntax, self.sourceInfo)
                        .eval(scope)
            },
    ).associateBy { it.name }

    operator fun get(name: String): WValue = builtins[name]!!
    operator fun contains(name: String): Boolean = name in builtins
}