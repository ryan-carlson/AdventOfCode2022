import java.io.File

enum class Fill {
    EMPTY, ROCK, SAND,
}

class Location(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

class Cave(private val floor: Boolean, rocks: List<String>) {
    private val cave = mutableMapOf<Location, Fill>()
    private val dropLocation = Location(500, 0)
    private var maxY:Int = dropLocation.y
    private var minX:Int = dropLocation.x
    private var maxX:Int = dropLocation.x

    init {
        for (input in rocks) {
            val rowValues = input.split("->")
            var lastX = -1
            var lastY = -1
            for (value in rowValues) {
                val cell = value.trim().split(',')
                val x = cell[0].toInt()
                val y = cell[1].toInt()
                if (lastX != -1 && lastY != -1) {
                    if (x == lastX) {
                        if (y > lastY) {
                            for (i in y downTo lastY) {
                                this.addRock(x=x, y=i)
                            }
                        } else {
                            for (i in lastY downTo y) {
                                this.addRock(x=x, y=i)
                            }
                        }
                    } else {
                        if (x > lastX) {
                            for (i in x downTo lastX) {
                                this.addRock(x=i, y=y)
                            }
                        } else {
                            for (i in lastX downTo x) {
                                this.addRock(x=i, y=y)
                            }
                        }
                    }
                }
                lastX = x
                lastY = y
            }
        }
    }

    private fun convert(fill: Fill): String {
        return when(fill) {
            Fill.EMPTY -> "."
            Fill.ROCK -> "x"
            Fill.SAND -> "o"
        }
    }

    fun getFill(x: Int, y: Int): Fill {
        if (floor && y >= maxY+2){
            return Fill.ROCK
        }
        return cave.getOrDefault(Location(x, y), Fill.EMPTY)
    }

    private fun addRock(x: Int, y: Int) {
        cave[Location(x, y)] = Fill.ROCK
        maxY = if (y > maxY) y else maxY
        minX = if (x < minX) x else minX
        maxX = if (x > maxX) x else maxX
    }

    private fun addSand(x: Int, y: Int) {
        cave[Location(x, y)] = Fill.SAND
        minX = if (x < minX) x else minX
        maxX = if (x > maxX) x else maxX
    }

    fun releaseSand(): Int {
        var count = 0
        while(dropSand()) {
            count++
            // Completely filled
            if (getFill(dropLocation.x, dropLocation.y) == Fill.SAND) {
                break
            }
        }
        return count
    }

    @Suppress("unused")
    fun output() {
        for (y in 0 .. maxY) {
            for (x in minX..maxX) {
                print(convert(getFill(x, y)))
                print(" ")
            }
            println()
        }
    }

    private fun dropSand(): Boolean {
        var x = dropLocation.x
        var y = dropLocation.y
        var settled = false
        while(!settled && y <= maxY+2) {
            if (getFill(x, y) == Fill.EMPTY) {
                y++
            } else if (getFill(x-1, y) == Fill.EMPTY) {
                y++
                x--
            } else if (getFill(x+1, y) == Fill.EMPTY) {
                y++
                x++
            } else {
                settled = true
            }
        }
        addSand(x, y-1)
        return settled
    }
}

fun main() {
    val inputs = loadResource("day-14-input")?.path?.let {
        File(it).readLines()
    }!!
    val cave = Cave(floor=false, rocks=inputs)
    println("Part One Result: ${cave.releaseSand()}")

    val cave2 = Cave(floor=true, rocks=inputs)
    println("Part Two Result: ${cave2.releaseSand()}")
    cave2.output()
}