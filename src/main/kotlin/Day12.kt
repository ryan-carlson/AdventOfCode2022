import structures.Graph
import structures.Vertex
import java.io.File

fun climbable(current: Char, target: Char): Boolean {
    return  getHeight(current)+1 >= getHeight(target)
}

fun climbable(current: CharVertex, target: CharVertex): Boolean {
    return climbable(current.data, target.data)
}

private fun getHeight(current: Char): Char {
   return when (current) {
        'S' -> 'a'
        'E' -> 'z'
        else -> current
    }
}

typealias CharVertex = Vertex<Int, Char>
typealias TopologyGraph = Graph<Int, Char>

fun loadGraph(): Pair<TopologyGraph, Map<Char, List<CharVertex>>> {
    val adjacencyListGraph = Graph<Int, Char>()
    val vertices: MutableMap<Char, List<CharVertex>> = mutableMapOf()
    loadResource("day-12-input")?.path?.let {
        File(it).readLines().fold(mutableListOf()) { acc: MutableList<MutableList<CharVertex>>, item ->
            acc.add(mutableListOf())
            val row = acc.last()
            val y = acc.size-1
            for ((x, value) in item.withIndex()) {
                val vertex = adjacencyListGraph.createVertex(adjacencyListGraph.getVerticesCount(), value)
                vertices[value] = if (vertices.contains(value)) vertices[value]!!.plus(vertex) else listOf(vertex)
                acc.last().add(vertex)
                if (x >= 1) {
                    val previousVertex = row[x-1]
                    if (climbable(vertex, previousVertex)) {
                        adjacencyListGraph.addDirectedEdge(vertex, previousVertex)
                    }
                    if (climbable(previousVertex, vertex)) {
                        adjacencyListGraph.addDirectedEdge(previousVertex, vertex)
                    }
                }
                if (y >= 1) {
                    val previousVertex = acc[y-1][x]
                    if (climbable(vertex, previousVertex)) {
                        adjacencyListGraph.addDirectedEdge(vertex, previousVertex)
                    }
                    if (climbable(previousVertex, vertex)) {
                        adjacencyListGraph.addDirectedEdge(previousVertex, vertex)
                    }
                }
            }
            acc
        }
    }
    return Pair(adjacencyListGraph, vertices)
}

fun main() {
    val (graph, vertices) = loadGraph()
    val start = vertices['S']!!.first()
    val end = vertices['E']!!.first()

    val result = graph.findShortestPath(start, end)
    println("Part One Result: ${result!!.vertices.size-1}")

    val lowestStartingPoints = vertices['a']!!
    var max = -1
    for (startingPoint in lowestStartingPoints) {
        val shortest = graph.findShortestPath(startingPoint, end)
        if (shortest != null) {
            val pathLength = shortest.vertices.size-1
            max = if (max == -1 || pathLength < max) pathLength else max
        }
    }
    println("Part Two Result: $max")
}