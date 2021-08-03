import type.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class WIntegrationTest {
    @Test
    fun std() {
        val repl = WRepl()
        repl.interpret("(define std (load \"std.wsp\"))")

        assertTrue(repl.interpret("((std '>=) 7 6.9)").truthy())
        assertTrue(repl.interpret("((std '>=) 7 7.1)").falsy())
        assertTrue(repl.interpret("((std '<=) 7 7.1)").truthy())
        assertTrue(repl.interpret("((std '<=) 7 6.9)").falsy())
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
        assertEquals(WList(WNumber(7), WList(WNumber(8), WNIL)), repl.interpret("(list 7 8)"))
    }

    @Test
    fun builtins() {
        val repl = WRepl()

        // Quote
        assertEquals(WSymbol("a"), repl.interpret("'a"))

        // Lambda
        assertEquals(
                WFunction(
                        repl.scope,
                        WList(WSymbol("a"), WList(WSymbol("b"), WNIL)),
                        WList(WSymbol("+"), WList(WSymbol("a"), WList(WSymbol("b"), WNIL)))),
                repl.interpret("(lambda (a b) (+ a b))"))

        // Cond
        assertEquals(WTrue(), repl.interpret("(cond (() ()) (t t))"))

        // List?
        assertEquals(WTrue(), repl.interpret("(list? (list 'a 'b))"))
        assertEquals(WTrue(), repl.interpret("(list? '(a b))"))
        assertEquals(WTrue(), repl.interpret("(list? '())"))
        assertEquals(WNIL, repl.interpret("(list? 'a)"))
    }

    @Test
    fun math() {
        val repl = WRepl()

        assertEquals(WNumber(14), repl.interpret("(+ 7 7)"))
        assertEquals(WNumber(49), repl.interpret("(* 7 7)"))
    }
}