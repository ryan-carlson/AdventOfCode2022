import utilities.loadResource
import java.io.File
import kotlin.math.abs

fun main() {

    var cycles = 0
    var register = 1
    var signalStrength = 0
    val crt = mutableListOf<Char>()

    loadResource("day-10-input").path.let {
        File(it).forEachLine { line ->
            val inputs = splitOnWhitespace(line)
            val cyclesRequired: Int
            val value: Int
            when(inputs[0]){
                "noop" -> {
                    cyclesRequired = 1
                    value = 0
                }
                "addx" -> {
                    cyclesRequired = 2
                    value = inputs[1].toInt()
                }
                else -> {
                    throw Error("Unexpected operation")
                }
            }
            for (i in 1..cyclesRequired) {
                crt.add(if (abs((cycles%40)-register) <= 1) '#' else '.')
                // Perform operation
                if (i == cyclesRequired) {
                    register += value
                }
                cycles++
                if ((cycles+20) % 40 == 0) {
                    signalStrength += cycles * register
                }
            }
        }
    }
    println("Part One Result: $signalStrength")

    println("Part Two Result:")
    for ((index, output) in crt.withIndex()) {
        print(output)
        if ((index+1) % 40 == 0) {
            println()
        }
    }
}
