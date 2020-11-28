import scope.WFunctionScope
import source.WProgramParser
import java.io.File

fun main(args: Array<String>) {
    if(args.isNotEmpty()) {
        val scope = WFunctionScope()
        val file = File(args[0])
        WProgramParser(file.absolutePath).parseAsProgram(file.readText())
                .eval(scope)
                .eval(scope)
    } else {
        WRepl().repl()
    }
}