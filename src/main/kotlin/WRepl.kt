import scope.WFunctionScope
import scope.WScope
import source.WEOFSourceInfo
import source.WProgramParser
import type.WNil
import type.WValue
import java.nio.file.Paths

class WRepl(val parser: WProgramParser = WProgramParser(), val scope: WScope = WFunctionScope()) {
    fun repl() {
        println("Wsp 0.1.")
        println()

        while(true) {
            print("wsp> ")
            val line = read() ?: return
            val result = try {
                interpret(line)
            } catch(ex: Exception) {
                ex.printStackTrace()
                WNil(WEOFSourceInfo(Paths.get(".").toAbsolutePath().toString()))
            }
            println("=> $result")
        }
    }

    private fun read(): String? {
        val statementBuilder = StringBuilder()
        do {
            val linePartial = readLine() ?: return null
            statementBuilder.appendLine(linePartial)
        } while(statementBuilder.toString().needsWork())
        return statementBuilder.toString()
    }

    private fun String.needsWork(): Boolean {
        var openParens = 0
        var closeParens = 0
        this.forEach { when (it) {
            '(' -> openParens++
            ')' -> closeParens++
        } }
        return openParens > closeParens
    }

    fun interpret(input: String): WValue {
        val code = parser.parseAsLine(input)
        val compiledCode = code.eval(scope)
        return compiledCode.eval(scope)
    }
}