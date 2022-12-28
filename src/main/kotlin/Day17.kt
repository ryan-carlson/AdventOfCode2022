import utilities.readLines

enum class CaveFill {
    EMPTY, ROCK
}

data class Coordinates(val x: Int, val y: Int)

data class Snapshot(val rockCount: Long, val maxHeight: Long)

// current y coordinate, block type, location in jet
data class CycleSnapshotId(val y: Int, val blockType: Long, val jetLocation: Int)

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
    private var cave: MutableList<Array<CaveFill>> = mutableListOf()
    private var jetsCurrent = jets
    private var maxHeight = 0
    private var compactedHeight = 0L
    private var rockCount = 1L
    private var uniqueCompactionsMap: MutableMap<CycleSnapshotId, Snapshot> = mutableMapOf()

    init {
        for (i in 1.. 3) {
            addEmptyRow ()
        }
    }

    private fun addEmptyRow() {
        cave.add(createRow())
    }

    private fun createRow(): Array<CaveFill> {
        return Array(width) { CaveFill.EMPTY }
    }

    private fun rowFilled(y: Int): Boolean {
        return cave[y].none { it == CaveFill.EMPTY }
    }

    fun dropRocks(count: Long): Long {
        var maxHeight = 0
        while (rockCount <= count) {
            val start = Coordinates(2, maxHeight+3)
            when (rockCount % 5) {
                1L ->  maxHeight = dropRock(LineRock(start), count)
                2L ->  maxHeight = dropRock(PlusRock(start), count)
                3L ->  maxHeight = dropRock(HockeyStickRock(start), count)
                4L ->  maxHeight = dropRock(VerticalLineRock(start), count)
                0L ->  maxHeight = dropRock(SquareRock(start), count)
            }
            rockCount++
        }
        if (debug) {
            print()
        }
        return maxHeight + compactedHeight
    }

    private fun dropRock(rock: Rock, count: Long): Int {
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
        val rows = mutableSetOf<Int>()
        for (location in rock.getLocations()) {
            rows.add(location.y)
            maxHeight = maxOf(location.y+1, maxHeight)
            cave[location.y][location.x] = CaveFill.ROCK
        }
        val updatedRowsSorted = rows.toMutableList()
        updatedRowsSorted.sortDescending()
        for (y in updatedRowsSorted) {
            if (rowFilled(y)) {
                compactCave(y, count)
                break
            }
        }
        return maxHeight
    }

    private fun compactCave(y: Int, count: Long) {
        val rowsToCompact = y+1
        maxHeight -= rowsToCompact
        compactedHeight += rowsToCompact
        cave = cave.subList(rowsToCompact, cave.size)

        val id = CycleSnapshotId(y, rockCount % 5, jetsCurrent.length)
        if (uniqueCompactionsMap.contains(id)) {
            val cycleCount = rockCount-uniqueCompactionsMap[id]!!.rockCount
            val cycleHeight = maxHeight+compactedHeight-uniqueCompactionsMap[id]!!.maxHeight
            val remainingFullCycles = (count-rockCount)/cycleCount
            rockCount += remainingFullCycles*cycleCount
            compactedHeight += remainingFullCycles*cycleHeight
        }
        uniqueCompactionsMap[id] = Snapshot(rockCount, maxHeight.toLong()+compactedHeight)
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

fun solveDay17Part1(lines: List<String>, rockCount: Long): Long {
    return VerticalCave(lines[0], false).dropRocks(rockCount)
}

fun solveDay17Part2(lines: List<String>, rockCount: Long): Long {
    return VerticalCave(lines[0], false).dropRocks(rockCount)
}

fun main() {
    val lines = readLines("day-17-input")
    println("Result Part One: ${solveDay17Part1(lines, 2022)}")
    println("Result Part Two: ${solveDay17Part2(lines, 1000000000000)}")
}