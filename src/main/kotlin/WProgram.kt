import type.WNil
import type.WValue
import type.init.WSyntaxList

data class WProgram(val lists: List<WValue>) {
    fun asSyntax(): WValue {
        return lists
                .foldRight(WNil() as WValue) { v, acc -> WSyntaxList(v, acc)
                        .apply { sourceInfo = v.sourceInfo } }
    }

    private fun compile(scope: WScope): WCompiledCode {
        val compiledCode = lists.map { it.eval(scope) }
        return WCompiledCode(compiledCode)
    }

    fun compileAndRun(): WValue {
        val scope = WFunctionScope()
        return compile(scope).eval(scope)
    }
}