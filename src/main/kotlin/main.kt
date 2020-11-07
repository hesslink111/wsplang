import java.io.File

fun main(args: Array<String>) {
    WispParser.program.parse(File(args[0]).readText()).eval()
}