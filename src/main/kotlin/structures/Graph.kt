package structures

class Graph<K, V> {

    private val adjacencyMap = mutableMapOf<Vertex<K, V>, ArrayList<Edge<K, V>>>()
    private val vertices = mutableMapOf<K, Vertex<K, V>>()

    data class Edge<K, V>(val source: Vertex<K, V>, val destination: Vertex<K, V>)

    fun createVertex(key: K, value: V): Vertex<K, V> {
        val vertex = Vertex(key, value)
        adjacencyMap[vertex] = arrayListOf()
        vertices[key] = vertex
        return vertex
    }

    fun getVerticesCount(): Int {
        return vertices.count()
    }

    fun getOrCreateVertex(key: K, value: V): Vertex<K, V> {
        if (vertices.contains(key)) {
            return vertices[key]!!
        }
        return createVertex(key, value)
    }

    fun getVertex(key: K): Vertex<K, V>? {
        return vertices[key]
    }

    fun addDirectedEdge(source: Vertex<K, V>, destination: Vertex<K, V>) {
        val edge = Edge(source, destination)
        adjacencyMap[source]?.add(edge)
    }

    fun getEdges(vertex: Vertex<K, V>): List<Edge<K, V>> {
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
