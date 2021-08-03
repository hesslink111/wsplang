import scope.WScope
import source.WProgramParser
import java.io.File

fun main(args: Array<String>) {
    if(args.isNotEmpty()) {
        val file = File(args[0]).absoluteFile
        val scope = WScope(file.parent)
        WProgramParser(file.absolutePath).parseAsProgram(file.readText())
                .eval(scope)
    } else {
        WRepl().repl()
    }
}