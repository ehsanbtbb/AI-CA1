import java.io.File

class Graph private constructor(
        val data: HashMap<Int, Pair<Vertex, MutableList<Pair<Vertex, Float>>>>) {
    override fun toString(): String {
        var graphRepresentation = "{\n"
        data.forEach {
            graphRepresentation += "{ id: ${it.key}, contains: (${it.value.first}), "
            graphRepresentation += "adjacents: ${it.value.second}}, \n"
        }
        return graphRepresentation
    }

    companion object {

        private fun createGraph(verticesFileName: String,
                edgesFileName: String): Pair<HashMap<Int, Vertex>, ArrayList<Edge>> {
            val vertices: HashMap<Int, Vertex> = hashMapOf()
            val edges: ArrayList<Edge> = arrayListOf()

            //Read the vertex related data from the specified file.
            File(verticesFileName).forEachLine {
                it.split(" ".toRegex()).takeIf { it.size == 3 }?.let {
                    val vertex = Vertex(it[0].toInt(), it[1].toFloat(), it[2].toFloat())
                    vertices.put(vertex.id, vertex)
                }
            }

            //Read the edge related data from the specified file.
            File(edgesFileName).forEachLine {
                it.split(" ".toRegex()).takeIf { it.size == 4 }?.let {
                    val edge = Edge(it[0].toInt(), it[1].toInt(), it[2].toInt(), it[3].toInt() == 1)
                    edges.add(edge)
                }
            }

            return Pair(vertices, edges)
        }

        fun createUnidirectionalUnweightedGraph(verticesFileName: String,
                edgesFileName: String): Graph {
            val graphData = createGraph(verticesFileName, edgesFileName)
            val graph: HashMap<Int, Pair<Vertex, MutableList<Pair<Vertex, Float>>>> = hashMapOf()

            graphData.first.forEach {
                graph[it.key] = Pair(it.value, arrayListOf())
            }

            graphData.second.forEach {
                val firstNode = graphData.first[it.nodeOneId]
                val secondNode = graphData.first[it.nodeTwoId]
                graph[it.nodeOneId]?.second?.add(
                        Pair(secondNode!!, secondNode.distanceTo(firstNode!!)))
                graph[it.nodeTwoId]?.second?.add(
                        Pair(firstNode!!, secondNode!!.distanceTo(firstNode)))
            }

            return Graph(graph)
        }

        fun createBidirectionalWeightedGraph(verticesFileName: String,
                edgesFileName: String): Graph {
            val graphData = createGraph(verticesFileName, edgesFileName)
            val graph: HashMap<Int, Pair<Vertex, MutableList<Pair<Vertex, Float>>>> = hashMapOf()

            graphData.first.forEach {
                graph[it.key] = Pair(it.value, arrayListOf())
            }

            graphData.second.forEach {
                val firstNode = graphData.first[it.nodeOneId]
                val secondNode = graphData.first[it.nodeTwoId]
                graph[it.nodeOneId]?.second?.add(
                        Pair(secondNode!!, secondNode.distanceTo(firstNode!!) / it.maxSpeed))
                if (!it.isOneWay) {
                    graph[it.nodeTwoId]?.second?.add(
                            Pair(firstNode!!, secondNode!!.distanceTo(firstNode) / it.maxSpeed))
                }
            }

            return Graph(graph)
        }

    }
}