package structures

class Path<K, V>(val vertices: List<Vertex<K, V>>) {
    fun add(element: Vertex<K, V>): Path<K, V> {
        return Path(vertices + element)
    }
}

class Graph<K, V> {

    private val adjacencyMap = mutableMapOf<Vertex<K, V>, ArrayList<Edge<K, V>>>()
    private val vertices = mutableMapOf<K, Vertex<K, V>>()

    data class Edge<K, V>(val source: Vertex<K, V>, val destination: Vertex<K, V>)

    fun findShortestPath(start: Vertex<K, V>, end: Vertex<K, V>): Path<K, V>? {
        return step(mutableListOf(Path(listOf(start))), end, mutableSetOf(start))
    }

    private tailrec fun step(paths: MutableList<Path<K, V>>, end: Vertex<K, V>, visited: MutableSet<Vertex<K, V>>): Path<K, V>? {
        val newPaths = mutableListOf<Path<K, V>>()
        var destinationPath: Path<K, V>? = null
        for (path in paths) {
            for (edge in this.getEdges(path.vertices.last())) {
                val destination = edge.destination
                if (destination == end) {
                    destinationPath = path.add(destination)
                }
                if (!visited.contains(destination)) {
                    newPaths.add(path.add(destination))
                    visited.add(destination)
                }
            }
        }

        return if (destinationPath != null || newPaths.isEmpty()) destinationPath else step(newPaths, end, visited)
    }

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

    private fun getEdges(vertex: Vertex<K, V>): List<Edge<K, V>> {
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
