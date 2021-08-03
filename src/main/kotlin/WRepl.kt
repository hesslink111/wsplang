import scope.WScope
import source.WLineColumnSourceInfo
import source.WProgramParser
import type.WNil
import type.WValue
import java.nio.file.Paths

class WRepl(
    private val parser: WProgramParser = WProgramParser(),
    val scope: WScope = WScope(Paths.get(".").toAbsolutePath().toString())
) {
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
                WNil(WLineColumnSourceInfo(Paths.get(".").toAbsolutePath().toString(), 0, 0))
            }
            println("=> $result")
            println()
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
        return parser.parseAsLine(input).eval(scope)
    }
}