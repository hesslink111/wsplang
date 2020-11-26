import source.WProgramParser
import java.io.File

fun main(args: Array<String>) {
    WProgramParser(File(args[0])).parse().compileAndRun()
}