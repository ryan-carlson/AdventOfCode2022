import structures.Graph
import structures.Path
import structures.Vertex
import utilities.loadResource
import java.io.File

data class Valve(val name: String, val pressure: Int, val adjacent: List<String>)

class PressureRelease(private val timeLimit: Int) {
    fun pressure(pressureRelease: Map<Int, Int>): Int {
        var total = 0
        for (release in pressureRelease) {
            total += (timeLimit-release.key)*release.value
        }
        return total
    }
}

//data class ValvePath(val path: List<ValveVertex>, val minutes: Int) {
//    private fun move(target: ValveVertex): ValvePath {
//        return this.copy(
//            minutes = minutes+1,
//            path = path + target,
//        )
//    }
//    fun traverse(shortest: Path<String, Valve>): ValvePath {
//        var valvePath = this
//        return this.copy(
//            minutes = minutes+shortest.vertices.size,
//            path = path + target,
//        )
//        for (i in 1 until shortest.vertices.size) {
//            val currentValve = shortest.vertices[i]
//            valvePath = valvePath.move(currentValve)
//            if (currentValve == shortest.vertices.last()) {
//                valvePath = valvePath.release()
//            }
//        }
//        return valvePath
//    }
//}

data class PathTracker(val path: List<ValveVertex>, val path2: List<ValveVertex>, val minutes: Int, val pressureRelease: Map<Int, Int>, val released: Set<ValveVertex>) {
    fun released(): Set<String> {
        return released.map { it.id }.toSet()
    }
//    fun minutes(): Int {
//        return minutes
//    }
    private fun move(target: ValveVertex): PathTracker {
        return this.copy(
            minutes = minutes+1,
            path = path + target,
        )
    }
    private fun release(): PathTracker {
        val last = path.last()
        val updatedMinutes = minutes+1
        return this.copy(
            minutes = updatedMinutes,
            pressureRelease = pressureRelease+mapOf(updatedMinutes to last.data.pressure),
            released = released + last,
        )
    }
//    fun last(): ValveVertex {
//        return path.last()
//    }
    fun traverse(shortest: Path<String, Valve>): PathTracker {
        var valvePath = this
        for (i in 1 until shortest.vertices.size) {
            val currentValve = shortest.vertices[i]
            valvePath = valvePath.move(currentValve)
            if (currentValve == shortest.vertices.last()) {
                valvePath = valvePath.release()
            }
        }
        return valvePath
    }
}

fun newValvePath(start: ValveVertex): PathTracker {
    return PathTracker(listOf(start), listOf(start), 0, mapOf(), setOf())
}

typealias ValveVertex = Vertex<String, Valve>

fun valveFromLine(line: String): Valve {
    val inputs = splitOnWhitespace(line)
    val adjacent = mutableListOf<String>()
    for (i in 9 until inputs.size) {
        adjacent.add(inputs[i].split(",")[0])
    }
    return Valve(name=inputs[1], pressure=parsePressure(inputs[4]), adjacent=adjacent.toList())
}

fun parsePressure(raw: String): Int {
    val rawValue = raw.split("=")[1]
    return rawValue.substring(0, rawValue.length-1).toInt()
}

fun initValves(lines: List<String>): Map<String, Valve> {
    return lines.map(::valveFromLine).associateBy { it.name }
}

fun initGraph(valves: Map<String, Valve>): Graph<String, Valve> {
    val caves = Graph<String, Valve>()
    valves.forEach { (key, value) ->
        val vertex = caves.getOrCreateVertex(key, value)
        for (edge in value.adjacent) {
            caves.addDirectedEdge(vertex, caves.getOrCreateVertex(edge, valves[edge]!!))
        }
    }
    return caves
}

class PressureLocator(lines: List<String>, private val timeLimit: Int) {

    private val pressureRelease = PressureRelease(timeLimit)
    private val valves = initValves(lines)
    private val caves = initGraph(valves)
    private val pressuredValves = valves.filter { valve -> valve.value.pressure > 0 }.map {it.key}.toList().toSet()

    fun findOptimalPathPressure(): Int {
        val paths = traverseNextValve(
            caves,
            listOf(newValvePath(caves.getVertex("AA")!!)),
        )
        val path = paths.reduce{acc, valvePath ->
            if (pressureRelease.pressure(valvePath.pressureRelease) > pressureRelease.pressure(acc.pressureRelease))
                valvePath
            else
                acc
        }
        return pressureRelease.pressure(path.pressureRelease)
    }

    private fun extend(pathTracker: PathTracker): List<PathTracker> {
        val extendedPaths = mutableListOf<PathTracker>()
        val valvesWithPressure = pressuredValves.subtract(pathTracker.released())
        for (valve in valvesWithPressure) {
            val shortest = caves.findShortestPath(pathTracker.path.last(), this.caves.getVertex(valve)!!)
            if (shortest != null) {
                val extended = pathTracker.traverse(shortest)
                if (extended.minutes <= timeLimit) {
                    extendedPaths.add(extended)
                }
            }
        }
        // path 2!!

        return extendedPaths
    }

    private fun traverseNextValve(caves: Graph<String, Valve>, paths: List<PathTracker>): List<PathTracker> {
        val extendedPaths = mutableListOf<PathTracker>()
        val completePaths = mutableListOf<PathTracker>()
        for (path in paths) {
            val extended = extend(path)
            if (extended.isEmpty()) {
                completePaths.add(path)
            } else {
                extendedPaths.addAll(extended)
            }
        }
        return if (extendedPaths.isNotEmpty()) traverseNextValve(caves, extendedPaths) + completePaths else completePaths
    }
}

fun solvePart1(lines: List<String>): Int {
    return PressureLocator(lines, 30).findOptimalPathPressure()
}

fun solvePart2(lines: List<String>): Int {
    return PressureLocator(lines, 26).findOptimalPathPressure()
}

fun main() {
    val lines = loadResource("day-16-input")?.path?.let {
        File(it).readLines()
    }!!

    println("Result Part One: ${solvePart1(lines)}")
    println("Result Part Two: ${solvePart2(lines)}")
}