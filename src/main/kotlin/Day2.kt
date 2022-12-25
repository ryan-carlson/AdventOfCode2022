import utilities.loadResource
import java.io.File

enum class RockPaperScissors {
    ROCK, PAPER, SCISSORS,
}

enum class Result {
    WIN, LOSE, DRAW,
}

const val RESULT_LOSS = 0
const val RESULT_TIE = 3
const val RESULT_WIN = 6

val map = mapOf(
    "A" to RockPaperScissors.ROCK,
    "B" to RockPaperScissors.PAPER,
    "C" to RockPaperScissors.SCISSORS,
)

val mapResults = mapOf(
    "X" to Result.LOSE,
    "Y" to Result.DRAW,
    "Z" to Result.WIN,
)

val values = mapOf(
    RockPaperScissors.ROCK to 1,
    RockPaperScissors.PAPER to 2,
    RockPaperScissors.SCISSORS to 3,
)

val valueToWin = mapOf(
    RockPaperScissors.ROCK to RockPaperScissors.PAPER,
    RockPaperScissors.PAPER to RockPaperScissors.SCISSORS,
    RockPaperScissors.SCISSORS to RockPaperScissors.ROCK,
)

val valueToLose = mapOf(
    RockPaperScissors.ROCK to RockPaperScissors.SCISSORS,
    RockPaperScissors.PAPER to RockPaperScissors.ROCK,
    RockPaperScissors.SCISSORS to RockPaperScissors.PAPER,
)

fun value(input: RockPaperScissors): Int {
    return values[input]!!
}

fun transform(input: String): RockPaperScissors {
    return map[input]!!
}

fun transformResultType(input: String): Result {
    return mapResults[input]!!
}

fun calculate(p1: RockPaperScissors, p2: Result): Pair<Int, Int> {
    val p1Value = value(p1)
    return when (p2) {
        Result.WIN -> {
            Pair(p1Value + RESULT_LOSS, value(valueToWin[p1]!!) + RESULT_WIN)
        }
        Result.LOSE -> {
            Pair(p1Value + RESULT_WIN, value(valueToLose[p1]!!) + RESULT_LOSS)
        }
        else -> {
            Pair(p1Value + RESULT_TIE, p1Value + RESULT_TIE)
        }
    }
}

fun calculate(p1: String, p2: String): Pair<Int, Int> {
    return calculate(transform(p1), transformResultType(p2))
}

fun main() {
    var p1TotalScore = 0
    var p2TotalScore = 0
    File(loadResource("day-2-input").path).forEachLine {line ->
        val values = splitOnWhitespace(line)
        val results = calculate(values[0], values[1])
        p1TotalScore += results.first
        p2TotalScore += results.second
    }
    println("P1 Score: $p1TotalScore")
    println("P2 Score: $p2TotalScore")
}