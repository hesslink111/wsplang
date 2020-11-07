import java.io.File

fun main(args: Array<String>) {
    WspParser.program.parse(File(args[0]).readText()).eval()
}