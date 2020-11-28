package source

import WProgram
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Scanners.isChar
import org.jparsec.Scanners.many
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns
import type.*
import type.init.WSyntaxList
import type.init.WSyntaxSymbol
import java.io.File

data class WProgramParser(val input: File) {
    fun parse(): WProgram {
        return program(input.absolutePath).parse(input.readText())
    }

    private fun program(filename: String): Parser<WProgram> {
        return Parsers
                .sequence(whitespace.asOptional(), wValue.followedBy(whitespace.asOptional()))
                .many()
                .map { values -> WProgram(filename, values) }
    }

    private val whitespace: Parser<Unit> by lazy {
        Parsers.or(
            Scanners.JAVA_LINE_COMMENT,
            Patterns.isChar(CharPredicates.IS_WHITESPACE).toScanner("whitespaces"))
            .many()
            .map { Unit } }

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
        ) { sl, _, _, _ -> WNil(WFileSourceInfo(input.absolutePath, sl)) } }

    private fun quotedWValue(wValueParser: Parser<WValue>): Parser<WValue> = Parsers.sequence(
        Parsers.SOURCE_LOCATION,
        Scanners.string("'"),
        wValueParser
    ) { sl, _, wv -> WSyntaxList(
            WSyntaxSymbol("quote", WFileSourceInfo(input.absolutePath, sl)),
            WSyntaxList(wv, WNil(WFileSourceInfo(input.absolutePath, sl))), WFileSourceInfo(input.absolutePath, sl)) }

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
        .foldRight(WNil(WFileSourceInfo(input.absolutePath, sl)) as WValue) { v, acc ->
            WSyntaxList(v, acc, v.sourceInfo) } }

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
        ) { sl, num -> WNumber(num, WFileSourceInfo(input.absolutePath, sl)) } }

    private val wString: Parser<WString> by lazy {
        Parsers.sequence(
            Parsers.SOURCE_LOCATION,
            Scanners.DOUBLE_QUOTE_STRING
        ) { sl, str -> WString(str.substring(1 until str.length-1), WFileSourceInfo(input.absolutePath, sl)) } }

    private val wSymbol: Parser<WSyntaxSymbol> by lazy {
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
        ) { sl, str -> WSyntaxSymbol(str, WFileSourceInfo(input.absolutePath, sl)) } }
}