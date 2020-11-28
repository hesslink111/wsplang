import type.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class WIntegrationTest {
    @Test
    fun std() {
        val repl = WRepl()
        repl.interpret("(load \"std.wsp\")")

        assertTrue(repl.interpret("(>= 7 6.9)").truthy())
        assertTrue(repl.interpret("(>= 7 7.1)").falsy())
        assertTrue(repl.interpret("(<= 7 7.1)").truthy())
        assertTrue(repl.interpret("(<= 7 6.9)").falsy())
    }

    @Test
    fun parse() {
        val repl = WRepl()

        // String
        assertEquals(WString("Hello"), repl.interpret("\"Hello\""))

        // Number
        assertEquals(WNumber(7), repl.interpret("7"))
        assertEquals(WNumber(7.0), repl.interpret("7.0"))
        assertNotEquals(WNumber(7), repl.interpret("7.0"))

        // List
        assertEquals(WList(WNumber(7), WList(WNumber(8), WNil())), repl.interpret("(list 7 8)"))
    }

    @Test
    fun math() {
        val repl = WRepl()

        assertEquals(WNumber(14), repl.interpret("(+ 7 7)"))
        assertEquals(WNumber(49), repl.interpret("(* 7 7)"))
    }
}