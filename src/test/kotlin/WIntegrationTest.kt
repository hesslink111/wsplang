import source.WProgramParser
import type.WNumber
import type.WString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class WIntegrationTest {
    @Test
    fun parse() {
        // String
        assertEquals(WProgramParser("\"Hello\"").parse().compileAndRun(), WString("Hello"))

        // Number
        assertEquals(WProgramParser("7").parse().compileAndRun(), WNumber(7))
        assertEquals(WProgramParser("7.0").parse().compileAndRun(), WNumber(7.0))
        assertNotEquals(WProgramParser("7.0").parse().compileAndRun(), WNumber(7))
    }

    @Test
    fun math() {
        assertEquals(WProgramParser("(+ 7 7)").parse().compileAndRun(), WNumber(14))
        assertEquals(WProgramParser("(* 7 7)").parse().compileAndRun(), WNumber(49))
    }
}