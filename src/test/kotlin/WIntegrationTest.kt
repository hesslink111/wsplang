import source.WProgramParser
import type.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class WIntegrationTest {
    @Test fun std() {
        fun withStd(contents: String) = "(load \"example/std.wsp\") $contents"
        assertTrue(WProgramParser(withStd("(>= 7 6.9)")).parse().compileAndRun().truthy())
        assertTrue(WProgramParser(withStd("(>= 7 7.1)")).parse().compileAndRun().falsy())
        assertTrue(WProgramParser(withStd("(<= 7 7.1)")).parse().compileAndRun().truthy())
        assertTrue(WProgramParser(withStd("(<= 7 6.9)")).parse().compileAndRun().falsy())
    }

    @Test
    fun parse() {
        // String
        assertEquals(WString("Hello"), WProgramParser("\"Hello\"").parse().compileAndRun())

        // Number
        assertEquals(WNumber(7), WProgramParser("7").parse().compileAndRun())
        assertEquals(WNumber(7.0), WProgramParser("7.0").parse().compileAndRun())
        assertNotEquals(WNumber(7), WProgramParser("7.0").parse().compileAndRun())

        // List
        assertEquals(WList(WNumber(7), WList(WNumber(8), WNil())), WProgramParser("(list 7 8)").parse().compileAndRun())
    }

    @Test
    fun math() {
        assertEquals(WNumber(14), WProgramParser("(+ 7 7)").parse().compileAndRun())
        assertEquals(WNumber(49), WProgramParser("(* 7 7)").parse().compileAndRun())
    }
}