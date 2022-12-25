import utilities.loadResource
import java.io.File
import java.util.*

fun initStack(values: List<String>): Stack<String> {
    val stack = Stack<String>()
    stack.addAll(values)
    return stack
}

fun init(): List<Stack<String>> {
    return listOf(
        initStack(listOf("R", "G", "H", "Q", "S", "B", "T", "N")),
        initStack(listOf("H", "S", "F", "D", "P", "Z", "J")),
        initStack(listOf("Z", "H", "V")),
        initStack(listOf("M", "Z", "J", "F", "G", "H")),
        initStack(listOf("T", "Z", "C", "D", "L", "M", "S", "R")),
        initStack(listOf("M", "T", "W", "V", "H", "Z", "J")),
        initStack(listOf("T", "F", "P", "L", "Z")),
        initStack(listOf("Q", "V", "W", "S")),
        initStack(listOf("W", "H", "L", "M", "T", "D", "N", "C")),
    )
}

fun part2(): String {
    val stacks = init()
    File(loadResource("day-5-input").path).forEachLine {line ->
        val values = splitOnWhitespace(line)
        if (values[0] == "move") {
            val move = values[1].toInt()
            val from = values[3].toInt()
            val to = values[5].toInt()
            var toMove = listOf<String>()
            repeat(move) {
                toMove = listOf(stacks[from-1].pop()) + toMove
            }
            for (crate in toMove) {
                stacks[to-1].push(crate)
            }
        }
    }
    var result = ""
    for (stack in stacks) {
        result += stack.peek()
    }
    return result
}

fun main() {
    val stacks = init()
    File(loadResource("day-5-input").path).forEachLine {line ->
        val values = splitOnWhitespace(line)
        if (values[0] == "move") {
            val move = values[1].toInt()
            val from = values[3].toInt()
            val to = values[5].toInt()
            repeat(move) {
                val crate = stacks[from-1].pop()
                stacks[to-1].push(crate)
            }
        }
    }
    var result = ""
    for (stack in stacks) {
        result += stack.peek()
    }
    println("Result Part 1: $result")

    val result2 = part2()
    println("Result Part 2: $result2")
}