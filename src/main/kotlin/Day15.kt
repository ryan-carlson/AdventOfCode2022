import utilities.loadResource
import java.io.File
import java.util.*
import kotlin.math.abs


fun insert(ranges: List<IntRange>, targetRange: IntRange): List<IntRange> {
    val result = mutableListOf<IntRange>()
    if (ranges.isEmpty()) {
        return listOf(targetRange)
    }
    var added = false
    for ((index, range) in ranges.withIndex()) {
        if (targetRange.first <= range.first && targetRange.last >= range.last) {
            result.add(targetRange)
            added = true
            continue
        }
        if (targetRange.last < range.first) {
            result.add(targetRange)
            added = true
        }
        result.add(range)
    }
    if (!added) {
        result.add(targetRange)
    }
    return result
}

class ProximityMap(inputs: List<String>) {

    private val locations: List<Pair<Location, Location>>
    private val beacons: Set<Location>

    private fun parseCoordinate(value: String): Int {
        val re = Regex("[^\\d ]")
        return re.replace( value.split("=")[1], "").toInt()
    }

    fun findAvailableLocation(boundary: Int): Location? {
        //var time = System.currentTimeMillis()
        //val xCoordinatesStatic = mutableSetOf<Int>()
//        for (x in 0 .. boundary) {
//            xCoordinatesStatic.add(x)
//        }
        for (y in 0..boundary) {
//            if (y % 100 == 0) {
//                val end = System.currentTimeMillis()
//                println(end-time)
//                time = end
//            }
            //val xCoordinates = HashSet(xCoordinatesStatic)
            val xRanges = LinkedList<IntRange>()
            for (location in locations) {
                val sensor = location.first
                val beacon = location.second
                val distance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
                val xDiff = abs(distance - abs(sensor.y - y ))
                val xRangeMin = sensor.x - xDiff
                val xRangeMax = sensor.x + xDiff
                val xRange = (if (xRangeMin < 0) 0 else xRangeMin)..(if (xRangeMax > boundary) boundary else xRangeMax)
                var targetIndex = 0
                if (xRanges.isEmpty()) {
                    xRanges.add(xRange)
                } else {
                    for ((index, range) in xRanges.withIndex()) {
                        // Already covered!
                        if (range.contains(xRange.first) && range.contains(xRange.last)) {
                            continue
                        }
                        if (range.contains(xRange.last)) {
                            xRanges.removeAt(index)
                            xRanges.add(xRange)
                        } else if (range.contains(xRange.first)) {
                            println("yes")
                        } else {
                            println("no")
                        }
                    }
                }
                if (xRanges.size == 1 && xRanges[0] == 0..boundary) {
                    continue
                }
            }
            println("$y: ${xRanges.size}")
        }
        return null
    }

    fun findAvailableLocationOld(boundary: Int): Location? {
        val open = mutableSetOf<Location>()
        for (y in 0 .. boundary) {
            if (y % 1000 == 0) {
                println("finding blocked for y location $y")
            }
            val blocked = plotLocations(y)
            for (x in 0 .. boundary) {
                val possible = Location(x, y)
                if (!blocked.contains(Location(x, y))) {
                    open.add(possible)
                }
            }
        }
        for (x in 0..boundary) {
            if (x % 100 == 0) {
                println("Checking x location $x")
            }
            for (y in 0..boundary) {
                var blocked = plotLocations(y)
                if (blocked.contains(Location(x, y))) {
                    continue
                }
                var available = true
                for (location in locations) {
                    val sensor = location.first
                    val beacon = location.second
                    val radius = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
                    if (abs(sensor.x - x) + abs(sensor.y - y) <= radius) {
                        available = false
                        break
                    }
                }
                if (available) {
                    return Location(x, y)
                }
            }
        }
        return null
    }

    fun plotLocationsWithBoundary(yTarget: Int, boundary: Int): Set<Location> {
        val none = mutableSetOf<Location>()
        for (location in locations) {
            val sensor = location.first
            val beacon = location.second
            val distance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
            val y = sensor.y
            val x = sensor.x
            val yStart = if (0 > y-distance) 0 else y-distance
            val yEnd = if (boundary < y+distance) boundary else y+distance
            for (yCoordinate in yStart .. yEnd) {
                if (yCoordinate != yTarget) {
                    continue
                }
                val xDiff = distance - abs(yCoordinate-y)
                val xStart = if (0 > x-xDiff) 0 else x-xDiff
                val xEnd = if (boundary < x+xDiff) boundary else x+xDiff
                for (xCoordinate in xStart .. xEnd) {
                    val location = Location(xCoordinate, yCoordinate)
                    if (!beacons.contains(location)) {
                        none.add(Location(xCoordinate, yCoordinate))
                    }
                }
            }
        }
        return none.toSet()
    }

    fun plotLocations(yTarget: Int): Set<Location> {
        val none = mutableSetOf<Location>()
        for (location in locations) {
            val sensor = location.first
            val beacon = location.second
            val distance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
            val y = sensor.y
            val x = sensor.x
            for (yCoordinate in y-distance .. y+distance) {
                if (yCoordinate != yTarget) {
                    continue
                }
                val xDiff = distance - abs(yCoordinate-y)
                for (xCoordinate in x-xDiff .. x+xDiff) {
                    val location = Location(xCoordinate, yCoordinate)
                    if (!beacons.contains(location)) {
                        none.add(Location(xCoordinate, yCoordinate))
                    }
                }
            }
        }
        return none.toSet()
    }

    init {
        val pairs = mutableListOf<Pair<Location, Location>>()
        val beaconSet= mutableSetOf<Location>()
        for (input in inputs) {
            val split = splitOnWhitespace(input)
            val sensor = Location(
                x=parseCoordinate(split[2]),
                y=parseCoordinate(split[3]),
            )
            val beacon = Location(
                x=parseCoordinate(split[8]),
                y=parseCoordinate(split[9]),
            )
            beaconSet.add(beacon)
            pairs.add(Pair(sensor, beacon))
        }
        locations = pairs.toList()
        beacons = beaconSet.toSet()
    }
}

fun main() {
    val inputs = loadResource("day-15-input")?.path?.let {
        File(it).readLines()
    }!!

    val proximityMap = ProximityMap(inputs = inputs)
    val result1 = proximityMap.plotLocations(yTarget = 2000000).size
    println("Part One Result: $result1")

    val boundary = 4_000_000
    val proximityMap2 = ProximityMap(inputs = inputs)
    val availableLocation = proximityMap2.findAvailableLocation(boundary)
    if (availableLocation != null) {
        val result2 = (availableLocation.x * boundary) + availableLocation.y
        println("Part Two Result: $result2")
    } else {
        println("Part Two Result: Not found")
    }
}