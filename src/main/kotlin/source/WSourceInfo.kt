package source

import org.jparsec.SourceLocation

interface WSourceInfo {
    fun location(): String
}

data class WFileSourceInfo(val filename: String, val sourceLocation: SourceLocation): WSourceInfo {
    override fun location() = "$filename:${sourceLocation.line}:${sourceLocation.column}"
}

data class WBuiltinSourceInfo(val builtinName: String): WSourceInfo {
    override fun location() = "builtin: $builtinName"
}

data class WEOFSourceInfo(val filename: String): WSourceInfo {
    override fun location() = "$filename:eof"
}