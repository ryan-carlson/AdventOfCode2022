import java.util.regex.Pattern

private val WHITESPACE = Pattern.compile("\\s+")

fun splitOnWhitespace(input: String): Array<String> {
    return WHITESPACE.split(input.trim())
}