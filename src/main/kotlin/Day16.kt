import structures.Graph
import structures.Path
import structures.Vertex
import utilities.readLines

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

data class ValvePath(val path: List<ValveVertex>, val minutes: Int, val pressureRelease: Map<Int, Int>) {
    fun last(): ValveVertex {
        return path.last()
    }

    fun traverse(value: Path<String, Valve>): ValvePath {
        val pathToTraverse = value.vertices
        val updatedMinutes = minutes + pathToTraverse.size
        return ValvePath(
            path = path + pathToTraverse.subList(1,pathToTraverse.size),
            minutes = updatedMinutes,
            pressureRelease = pressureRelease + mapOf(updatedMinutes to pathToTraverse.last().data.pressure),
        )
    }
}

data class PathTracker(val paths: Pair<ValvePath, ValvePath?>, val released: Set<ValveVertex>, val pressureRelease: PressureRelease) {
    fun released(): Set<String> {
        return released.map { it.id }.toSet()
    }
    fun release(): PathTracker {
        val last = paths.first.last()
        if (paths.second != null) {
            val secondPath = paths.second!!
            val last2 = secondPath.last()
            return this.copy(
                released = released + last + last2,
            )
        }
        return this.copy(
            released = released + last,
        )
    }

    fun pressure(): Int {
        if (paths.second != null) {
            return pressureRelease.pressure(paths.first.pressureRelease) + pressureRelease.pressure(paths.second!!.pressureRelease)
        }
        return pressureRelease.pressure(paths.first.pressureRelease)
    }
}

fun newValvePaths(start: ValveVertex, elephant: Boolean): Pair<ValvePath, ValvePath?> {
    return if (elephant) {
        Pair(newValvePath(start), newValvePath(start))
    } else {
        Pair(newValvePath(start), null)
    }
}

fun newValvePath(start: ValveVertex): ValvePath {
    return ValvePath(listOf(start), 0, mapOf())
}

fun newPathTracker(start: ValveVertex, elephant: Boolean, pressureRelease: PressureRelease): PathTracker {
    return PathTracker(newValvePaths(start, elephant), setOf(), pressureRelease)
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

class PressureLocator(lines: List<String>, private val elephant: Boolean) {

    private val timeLimit = if (elephant) 26 else 30
    private val pressureRelease = PressureRelease(timeLimit)
    private val valves = initValves(lines)
    private val caves = initGraph(valves)
    private val pressuredValves = valves.filter { valve -> valve.value.pressure > 0 }.map {it.key}.toList().toSet()
    private val shortestPaths: MutableMap<String, Path<String, Valve>> = mutableMapOf()
    private var totalPaths = 0

    fun findOptimalPathPressure(): Int {
        return traverseNextValve(
            caves,
            newPathTracker(caves.getVertex("AA")!!, elephant, pressureRelease),
            0,
        )
    }

    private fun extend(path: Pair<ValvePath, ValvePath?>, valve: String, remainingValves: Set<String>): List<Pair<ValvePath, ValvePath?>> {
        val paths = mutableListOf<Pair<ValvePath, ValvePath?>>()
        val start = path.first.last()
        val end =  this.caves.getVertex(valve)!!
        val pathKey = "${start.id}->${end.id}"
        val shortest = if (shortestPaths.contains(pathKey)) {
            shortestPaths[pathKey]
        } else {
            caves.findShortestPath(path.first.last(), this.caves.getVertex(valve)!!)
        }
        if (shortest != null) {
            shortestPaths[pathKey] = shortest
            val extended = path.first.traverse(shortest)
            if (extended.minutes <= timeLimit) {
                var secondPathAdded = false
                if (path.second != null) {
                    for (remainingValve in remainingValves.subtract(setOf(valve))) {
                        val secondPath = path.second!!
                        val shortestSecondPath = caves.findShortestPath(secondPath.last(), this.caves.getVertex(remainingValve)!!)
                        if (shortestSecondPath != null) {
                            val extendedSecondPath = secondPath.traverse(shortestSecondPath)
                            if (extendedSecondPath.minutes <= timeLimit) {
                                paths.add(Pair(extended, extendedSecondPath))
                                secondPathAdded = true
                            }
                        }
                    }
                }
                if (!secondPathAdded){
                    paths.add(Pair(extended, null))
                }
            } else {
                if (path.second != null) {
                    val secondPath = path.second!!
                    val shortestSecondPath = caves.findShortestPath(secondPath.last(), this.caves.getVertex(valve)!!)
                    if (shortestSecondPath != null) {
                        val extendedSecondPath = secondPath.traverse(shortestSecondPath)
                        if (extendedSecondPath.minutes <= timeLimit) {
                            paths.add(Pair(path.first, extendedSecondPath))
                        }
                    }
                }
            }
        }
        return paths
    }

    private fun extend(pathTracker: PathTracker): List<PathTracker> {
        val trackers = mutableListOf<PathTracker>()
        val valvesWithPressure = pressuredValves.subtract(pathTracker.released())
        for (valve in valvesWithPressure) {
            trackers.addAll(extend(pathTracker.paths, valve, valvesWithPressure).map { pathTracker.copy(paths=it).release() })
        }
        return trackers
    }

    private fun traverseNextValve(caves: Graph<String, Valve>, path: PathTracker, pressure: Int): Int {
        val extended = extend(path)
        var maxPressure = pressure
        if (extended.isEmpty()) {
            maxPressure = maxOf(path.pressure(), maxPressure)
            totalPaths++
            if (totalPaths % 100000 == 0) {
                println("Paths: $totalPaths")
            }
        } else {
            for (extendedPath in extended) {
                maxPressure = maxOf(traverseNextValve(caves, extendedPath, maxPressure), maxPressure)
                totalPaths++
                if (totalPaths % 100000 == 0) {
                    println("Paths: $totalPaths")
                }
            }
        }
        return maxPressure
    }
}

fun solvePart1(lines: List<String>): Int {
    return PressureLocator(lines, false).findOptimalPathPressure()
}

fun solvePart2(lines: List<String>): Int {
    return PressureLocator(lines, true).findOptimalPathPressure()
}

fun main() {
    val lines = readLines("day-16-input")
    println("Result Part One: ${solvePart1(lines)}")
    println("Result Part Two: ${solvePart2(lines)}")
}