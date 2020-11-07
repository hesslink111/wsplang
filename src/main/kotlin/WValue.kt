import org.jparsec.SourceLocation

interface WValue {
    var sourceLocation: SourceLocation?
    fun head(): WValue
    fun tail(): WValue
    fun eval(scope: WScope): WValue
}