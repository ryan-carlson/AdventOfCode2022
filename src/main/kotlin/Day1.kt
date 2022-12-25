import utilities.loadResource
import java.io.File
import java.util.*


class CalorieComparator {
    companion object : Comparator<Int> {
        override fun compare(a: Int, b: Int): Int = b - a
    }
}

fun main() {
    val caloriesPerElf: PriorityQueue<Int> = PriorityQueue<Int>(CalorieComparator)
    var maxCalories = 0
    var currentCalories = 0
    File(loadResource("day-1-input").path).forEachLine {
        val trimmed = it.trim()
        if (trimmed.isEmpty()) {
            caloriesPerElf.add(currentCalories)
            currentCalories = 0
        } else {
            currentCalories += trimmed.toInt()
        }
        maxCalories = if (currentCalories > maxCalories) currentCalories else maxCalories
    }

    val elfList = caloriesPerElf.toList()
    println("Part 1 Result: $maxCalories")
    val top3Calories = elfList[0] + elfList[1] + elfList[2]
    println("Part 2 Result: $top3Calories")
}