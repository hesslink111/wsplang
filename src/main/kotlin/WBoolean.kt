import org.jparsec.SourceLocation

interface WBoolean {
    companion object {
        fun from(sl: SourceLocation?, boolean: Boolean) = if(boolean) WSymbol("t") else WNil().also { it.sourceLocation = sl }
    }
}
