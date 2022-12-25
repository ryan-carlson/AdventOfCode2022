import utilities.loadResource
import java.io.File

fun rangeSet(input: String): Set<Int> {
    val rangeValues = input.trim().split("-")
    return IntRange(rangeValues[0].toInt(), rangeValues[1].toInt()).toSet()
}

fun isContained(elf1: String, elf2: String): Boolean {
    val range1 = rangeSet(elf1)
    val range2 = rangeSet(elf2)
    if (range1.containsAll(range2) || range2.containsAll(range1)) {
        return true
    }
    return false
}

fun isIntersecting(elf1: String, elf2: String): Boolean {
    val range1 = rangeSet(elf1)
    val range2 = rangeSet(elf2)
    return range1.intersect(range2).isNotEmpty()
}

fun main() {
    var isContainedCount = 0
    var isIntersectingCount = 0
    File(loadResource("day-4-input").path).forEachLine {
        val assignments = it.trim().split(",")
        if (isContained(assignments[0], assignments[1])) {
            isContainedCount++
        }
        if (isIntersecting(assignments[0], assignments[1])) {
            isIntersectingCount++
        }
    }
    println("Part 1 result: $isContainedCount")
    println("Part 2 result: $isIntersectingCount")
}