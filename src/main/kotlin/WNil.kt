data class WNil(val unit: Unit = Unit): WValue {
    override var sourceInfo: WSourceInfo? = null
    override fun head() = this
    override fun tail() = this
    override fun eval(scope: WScope) = this
    override fun invoke(scope: WScope, rawArguments: WValue) = throw IllegalArgumentException("Cannot invoke nil")
    override fun toString(): String = "nil"
}