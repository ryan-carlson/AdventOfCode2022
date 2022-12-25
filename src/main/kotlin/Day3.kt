import utilities.loadResource
import java.io.File

fun getPriority(input: Char): Int {
    return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(input) + 1
}

fun splitCompartments(input: String): Pair<String, String> {
    val mid: Int = input.length / 2
    return Pair(input.substring(0, mid), input.substring(mid))
}

fun findGroupBadge(groupRuckSacks: List<String>): Char? {
    val elf1 = groupRuckSacks[0].toSet()
    val elf2 = groupRuckSacks[1].toSet()
    val elf3 = groupRuckSacks[2].toSet()
    for (item: Char in elf1) {
        if (elf2.contains(item) && elf3.contains(item)) {
            return item
        }
    }
    return null
}

fun main() {
    var totalPriority = 0
    File(loadResource("day-3-input").path).forEachLine {
        val compartments = splitCompartments(it.trim())
        val result = compartments.first.toSet().intersect(compartments.second.toSet())
        totalPriority += getPriority(result.first())
    }
    println("Result Part 1: $totalPriority")

    var groupRuckSacks = listOf<String>()
    var totalBadgePriority = 0
    File(loadResource("day-3-input").path).forEachLine {
        val trimmed = it.trim()
        groupRuckSacks = groupRuckSacks + trimmed
        if (groupRuckSacks.size == 3) {
            totalBadgePriority += getPriority(findGroupBadge(groupRuckSacks)!!)
            groupRuckSacks = listOf()
        }
    }
    println("Result Part 2: $totalBadgePriority")
}