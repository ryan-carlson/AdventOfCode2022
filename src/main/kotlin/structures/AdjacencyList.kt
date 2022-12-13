package structures

data class Vertex<T>(val index: Int, val data: T)

class AdjacencyList<T> {

    private val adjacencyMap = mutableMapOf<Vertex<T>, ArrayList<Edge<T>>>()

    data class Edge<T>(val source: Vertex<T>, val destination: Vertex<T>, val weight: Double? = null)

    fun createVertex(data: T): Vertex<T> {
        val vertex = Vertex(adjacencyMap.count(), data)
        adjacencyMap[vertex] = arrayListOf()
        return vertex
    }

    fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double? = 0.0) {
        val edge = Edge(source, destination, weight)
        adjacencyMap[source]?.add(edge)
    }

    fun getEdges(vertex: Vertex<T>): List<Edge<T>> {
        return adjacencyMap.getOrElse(vertex) { listOf() }
    }

    override fun toString(): String {
        return buildString {
            adjacencyMap.forEach { (vertex, edges) ->
                val edgeString = edges.joinToString { it.destination.data.toString() }
                append("${vertex.data} -> [$edgeString]\n")
            }
        }
    }
}