package source

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns
import type.*
import java.nio.file.Paths

data class WProgramParser(val absolutePath: String) {
    constructor(): this(Paths.get(".").toAbsolutePath().toString())

    fun parseAsProgram(input: String): WValue {
        // Should be a begin statement.
        val lists = program().parse(input)
        val syntax = lists.foldRight(WNil(WLineColumnSourceInfo(absolutePath, 0, 0)) as WValue) { v, acc ->
            WList(v, acc, v.sourceInfo)
        }
        return WList(WSymbol("begin", WLineColumnSourceInfo(absolutePath, 0, 0)), syntax, WLineColumnSourceInfo(absolutePath, 0, 0))
    }

    fun parseAsLine(input: String): WValue {
        // Should be whatever is in the thing.
        return wValue.followedBy(whitespace.asOptional()).parse(input)
    }

    private fun program(): Parser<List<WValue>> {
        return Parsers
                .sequence(whitespace.asOptional(), wValue.followedBy(whitespace.asOptional()))
                .many()
    }

    private val whitespace: Parser<Unit> by lazy {
        Parsers.or(
            Scanners.JAVA_LINE_COMMENT,
            Patterns.isChar(CharPredicates.IS_WHITESPACE).toScanner("whitespaces"))
            .many()
            .map { } }

    private val wValue: Parser<WValue> by lazy {
        val wValueRef = Parser.newReference<WValue>()
        val wValueParse = Parsers.or(
            nil,
            quotedWValue(wValueRef.lazy()),
            wSyntaxList(wValueRef.lazy()),
            wNumber,
            wString,
            wSymbol)
        wValueRef.set(wValueParse)

        wValueParse
    }

    private val nil: Parser<WNil> by lazy {
        Parsers.sequence(
            Parsers.SOURCE_LOCATION,
            Scanners.string("("),
            whitespace,
            Scanners.string(")")
        ) { sl, _, _, _ -> WNil(WFileSourceInfo(absolutePath, sl)) } }

    private fun quotedWValue(wValueParser: Parser<WValue>): Parser<WValue> = Parsers.sequence(
        Parsers.SOURCE_LOCATION,
        Scanners.string("'"),
        wValueParser
    ) { sl, _, wv -> WList(
            WSymbol("quote", WFileSourceInfo(absolutePath, sl)),
            WList(wv, WNil(WFileSourceInfo(absolutePath, sl)), WFileSourceInfo(absolutePath, sl)), WFileSourceInfo(absolutePath, sl)) }

    private fun wSyntaxList(wValueParser: Parser<WValue>): Parser<WValue> = Parsers.sequence(
        Parsers.SOURCE_LOCATION,
        Scanners.string("("),
        whitespace,
        Parsers.sequence(
            wValueParser,
            whitespace) { wv, _ ->
                wv
            }.many(),
        Scanners.string(")"),
        whitespace
    ) { sl, _, _, wvs, _, _ -> wvs
        .foldRight(WNil(WFileSourceInfo(absolutePath, sl)) as WValue) { v, acc ->
            WList(v, acc, v.sourceInfo) } }

    private val wNumber: Parser<WNumber> by lazy {
        Parsers.sequence(
                Parsers.SOURCE_LOCATION,
                Parsers.or(
                        Scanners.INTEGER
                                .followedBy(Scanners.string("."))
                                .followedBy(Scanners.INTEGER).source()
                                .map { num -> num.toDouble() },
                        Scanners.INTEGER.map { num -> num.toInt() }
                ),
        ) { sl, num -> WNumber(num, WFileSourceInfo(absolutePath, sl)) } }

    private val wString: Parser<WString> by lazy {
        Parsers.sequence(
            Parsers.SOURCE_LOCATION,
            Scanners.DOUBLE_QUOTE_STRING
        ) { sl, str -> WString(str.substring(1 until str.length-1), WFileSourceInfo(absolutePath, sl)) } }

    private val wSymbol: Parser<WValue> by lazy {
        Parsers.sequence(
            Parsers.SOURCE_LOCATION,
            Parsers.or(
                    Scanners.string("-"),
                    Scanners.string("+"),
                    Scanners.string("-"),
                    Scanners.string("*"),
                    Scanners.string("/"),
                    Scanners.string("@"),
                    Scanners.string("$"),
                    Scanners.string("%"),
                    Scanners.string("^"),
                    Scanners.string("&"),
                    Scanners.string("_"),
                    Scanners.string("="),
                    Scanners.string("<"),
                    Scanners.string(">"),
                    Scanners.string("~"),
                    Scanners.string("."),
                    Scanners.string("?"),
                    Scanners.IDENTIFIER).many1().source()
        ) { sl, str ->
            return@sequence if(str == "t") {
                WTrue(WFileSourceInfo(absolutePath, sl))
            } else {
                WSymbol(str, WFileSourceInfo(absolutePath, sl))
            } } }
}