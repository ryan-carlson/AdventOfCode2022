import utilities.loadResource
import java.io.File
import kotlin.math.abs

class Position(x: Int, y: Int) {
    private val position = Pair(x, y)

    private fun getX(): Int {
        return position.first
    }

    private fun getY(): Int {
        return position.second
    }

    fun move(x: Int, y: Int): Position {
        return Position(position.first + x, position.second + y)
    }

    fun follow(followed: Position): Position {
        val xDiff = followed.getX()-getX()
        val yDiff = followed.getY()-getY()
        val xDiffAbs = abs(xDiff)
        val yDiffAbs = abs(yDiff)
        return if (xDiffAbs+yDiffAbs >= 3) {
            val xMove = if (xDiff < 0) -1 else 1
            val yMove = if (yDiff < 0) -1 else 1
            Position(getX()+xMove, getY()+yMove)
        } else if (xDiffAbs == 2) {
            val xMove = if (xDiff < 0) -1 else 1
            Position(getX()+xMove, getY())
        } else if (yDiffAbs == 2) {
            val yMove = if (yDiff < 0) -1 else 1
            Position(getX(), getY()+yMove)
        }  else {
            Position(getX(), getY())
        }

    }

    override fun toString(): String {
        return "Position(x=${position.first}, y=${position.second})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

class Grid(private val knotCount: Int) {
    private val knots = initKnots(knotCount)
    private val tailVisited = mutableSetOf(knots.last())

    private fun initKnots(count: Int): MutableList<Position> {
        val knots = mutableListOf<Position>()
        for (i in 1..count) {
            knots += Position(0, 0)
        }
        return knots
    }

    fun move(moveX: Int, moveY: Int, steps: Int) {
        for (i in 1..steps) {
            knots[0] = knots[0].move(moveX, moveY)
            for (j in 1 until knotCount) {
                knots[j] = knots[j].follow(knots[j-1])
            }
            tailVisited += knots.last()
        }
    }

    fun getTailVisitedCount(): Int {
        return tailVisited.size
    }
}

fun main() {

    val directions = hashMapOf(
        "L" to Pair(-1, 0),
        "R" to Pair(1, 0),
        "U" to Pair(0, 1),
        "D" to Pair(0, -1),
    )

    val gridPart1 = Grid(2)
    val gridPart2 = Grid(10)

    loadResource("day-9-input").path.let {
        File(it).forEachLine { line ->
            val inputs = splitOnWhitespace(line)
            val (moveX, moveY) = directions[inputs[0]]!!
            val steps = inputs[1].toInt()
            gridPart1.move(moveX, moveY, steps)
            gridPart2.move(moveX, moveY, steps)
        }
    }
    println("Part One Result: ${gridPart1.getTailVisitedCount()}")
    println("Part Two Result: ${gridPart2.getTailVisitedCount()}")
}