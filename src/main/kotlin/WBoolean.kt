interface WBoolean {
    companion object {
        fun from(si: WSourceInfo?, boolean: Boolean) = if(boolean) WSymbol("t") else WNil().also { it.sourceInfo = si }
    }
}
