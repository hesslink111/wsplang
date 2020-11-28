import scope.WFunctionScope
import scope.WScope
import source.WBuiltinSourceInfo
import source.WEOFSourceInfo
import type.WNil
import type.WValue
import type.init.WSyntaxList

data class WProgram(val filename: String, val lists: List<WValue>) {
    fun asSyntax(): WValue {
        return lists.foldRight(WNil(WEOFSourceInfo(filename)) as WValue) { v, acc -> WSyntaxList(v, acc, v.sourceInfo) }
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