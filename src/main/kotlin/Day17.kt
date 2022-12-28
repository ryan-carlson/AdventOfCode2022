import utilities.readLines
import java.util.*

enum class CaveFill {
    EMPTY, ROCK
}

data class Coordinates(val x: Int, val y: Int)

class LineRock(override var coordinates: Coordinates): Rock(
    coordinates,
    listOf(
        Coordinates(0, 0),
        Coordinates(1, 0),
        Coordinates(2, 0),
        Coordinates(3, 0),
    ),
) {
    override fun getWidth(): Int {
        return 4
    }
}

class PlusRock(override var coordinates: Coordinates): Rock(
    coordinates,
    listOf(
        Coordinates(1, 0),
        Coordinates(0, 1),
        Coordinates(1, 1),
        Coordinates(2, 1),
        Coordinates(1, 2),
    ),
) {
    override fun getWidth(): Int {
        return 3
    }
}

class HockeyStickRock(override var coordinates: Coordinates): Rock(
    coordinates,
    listOf(
        Coordinates(0, 0),
        Coordinates(1, 0),
        Coordinates(2, 0),
        Coordinates(2, 1),
        Coordinates(2, 2),
    ),
) {
    override fun getWidth(): Int {
        return 3
    }
}

class VerticalLineRock(override var coordinates: Coordinates): Rock(
    coordinates,
    listOf(
        Coordinates(0, 0),
        Coordinates(0, 1),
        Coordinates(0, 2),
        Coordinates(0, 3),
    ),
) {
    override fun getWidth(): Int {
        return 1
    }
}

class SquareRock(override var coordinates: Coordinates): Rock(
    coordinates,
    listOf(
        Coordinates(0, 0),
        Coordinates(0, 1),
        Coordinates(1, 0),
        Coordinates(1, 1),
    ),
) {
    override fun getWidth(): Int {
        return 2
    }
}

sealed class Rock(open var coordinates: Coordinates, private val locs: List<Coordinates>) {
    fun getLocations(): List<Coordinates> {
        return locs.map { loc -> Coordinates(loc.x+coordinates.x, loc.y+coordinates.y) }
    }
    abstract fun getWidth(): Int
    fun moveLeft() {
        coordinates = Coordinates(x=coordinates.x-1,y=coordinates.y)
    }
    fun moveRight() {
        coordinates = Coordinates(x=coordinates.x+1,y=coordinates.y)
    }
    fun moveDown() {
        coordinates = Coordinates(x=coordinates.x,y=coordinates.y-1)
    }
}

class VerticalCave(private val jets: String, private val debug: Boolean) {

    private val width = 7
    private val cave = Stack<Array<CaveFill>>()
    private var jetsCurrent = jets
    var maxHeight = 0

    init {
        for (i in 1.. 3) {
            addEmptyRow ()
        }
    }

    private fun addEmptyRow() {
        cave.push(createRow())
    }

    private fun createRow(): Array<CaveFill> {
        return Array(width) { CaveFill.EMPTY }
    }

    fun dropRocks(count: Long): Int {
        var maxHeight = 0
        for (i in 1..count) {
            val start = Coordinates(2, maxHeight+3)
            when (i % 5) {
                1L ->  maxHeight = dropRock(LineRock(start))
                2L ->  maxHeight = dropRock(PlusRock(start))
                3L ->  maxHeight = dropRock(HockeyStickRock(start))
                4L ->  maxHeight = dropRock(VerticalLineRock(start))
                0L ->  maxHeight = dropRock(SquareRock(start))
            }
        }
        if (debug) {
            print()
        }
        return maxHeight
    }

    private fun dropRock(rock: Rock): Int {
        for (location in rock.getLocations()) {
            for (i in cave.size..location.y) {
                addEmptyRow()
            }
        }
        var settled = false
        while (!settled) {
            jet(rock)
            settled = fall(rock)
        }
        val rowsUpdated = mutableSetOf<Int>()
        for (location in rock.getLocations()) {
            rowsUpdated.add(location.y)
            maxHeight = maxOf(location.y+1, maxHeight)
            cave[location.y][location.x] = CaveFill.ROCK
        }
        for (y in rowsUpdated) {
            // Check if full and adjust cave
        }
        return maxHeight
    }

    private fun fall(rock: Rock): Boolean {
        var settled = false
        for (location in rock.getLocations()) {
            if (rock.coordinates.y==0 || cave[location.y-1][location.x] == CaveFill.ROCK) {
                settled = true
            }
        }
        if (!settled) {
            rock.moveDown()
        }
        return settled
    }

    private fun canMoveRight(rock: Rock): Boolean {
        if (rock.coordinates.x + rock.getWidth() >= 7) {
            return false
        }
        for (location in rock.getLocations()) {
            if (cave[location.y][location.x+1] == CaveFill.ROCK){
                return false
            }
        }
        return true
    }

    private fun canMoveLeft(rock: Rock): Boolean {
        if (rock.coordinates.x == 0) {
            return false
        }
        for (location in rock.getLocations()) {
            if (cave[location.y][location.x-1] == CaveFill.ROCK){
                return false
            }
        }
        return true
    }

    private fun jet(rock: Rock) {
        if (jetsCurrent.isEmpty()) {
            jetsCurrent = jets
        }
        val direction = jetsCurrent.first()
        jetsCurrent = jetsCurrent.drop(1)
        when(direction) {
            '<' -> {
                if (canMoveLeft(rock)) {
                    rock.moveLeft()
                }
            }
            '>' -> {
                if (canMoveRight(rock)) {
                    rock.moveRight()
                }
            }
        }
    }

    private fun printRow(caveFills: Array<CaveFill>) {
        for (fill in caveFills) {
            when(fill) {
                CaveFill.EMPTY -> print(".")
                CaveFill.ROCK -> print("#")
            }
        }
    }

    private fun print() {
        for (i in cave.size-1 downTo 0) {
            printRow(cave[i])
            println()
        }
    }
}

fun solveDay17Part1(lines: List<String>, rockCount: Long): Int {
    return VerticalCave(lines[0], true).dropRocks(rockCount)
}

fun solveDay17Part2(lines: List<String>, rockCount: Long): Int {
    return VerticalCave(lines[0], true).dropRocks(rockCount)
}

fun main() {
    val lines = readLines("day-17-input")
    println("Result Part One: ${solveDay17Part1(lines, 2022)}")
    println("Result Part Two: ${solveDay17Part2(lines, 1000000000000)}")
}