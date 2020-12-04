import source.WFileSourceInfo
import source.WProgramParser
import type.*
import type.init.WSyntaxList
import type.init.WSyntaxSymbol
import java.nio.file.Path

object WInitBuiltins {
    private val builtins = listOf(
        WBuiltinFunction("defmacro") { self, scope, rawArguments ->
            val signature = rawArguments.head() as WSyntaxList
            val name = signature.head() as WSyntaxSymbol
            val parameters = signature.tail().convertToWList(scope)
            val body = rawArguments.tail().head().eval(scope)
            val macro = WMacroFunction(scope, parameters, body, self.sourceInfo)
            scope[name] = macro
            macro
        },
        WBuiltinFunction("load") { _, scope, rawArguments ->
            val filename = rawArguments.head() as WString
            val filePath = Path.of(filename.value)
            val file = if(filePath.isAbsolute) {
                filePath.toFile()
            } else {
                Path.of((rawArguments.sourceInfo as WFileSourceInfo).filename).resolveSibling(filePath).toFile()
            }
            WProgramParser(file.absolutePath).parseAsProgram(file.readText()).eval(scope)
        },
    ).associateBy { it.name }

    operator fun get(name: String): WValue = builtins[name]!!
    operator fun contains(name: String): Boolean = name in builtins
}