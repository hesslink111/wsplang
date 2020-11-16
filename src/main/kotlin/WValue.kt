import org.jparsec.SourceLocation

interface WValue {
    var sourceLocation: SourceLocation?
    fun head(): WValue
    fun tail(): WValue
    fun eval(scope: WScope): WValue
    fun invoke(scope: WScope, rawArguments: WValue): WValue
}

fun WValue.map(f: (WValue) -> WValue): WValue = if(this is WNil) {
    WNil()
} else {
    WList(f(head()), tail().map(f))
}.also { it.sourceLocation = sourceLocation }

fun WValue.forEach(f: (WValue) -> Unit): Unit {
    if(this !is WNil) {
        f(head())
        tail().forEach(f)
    }
}