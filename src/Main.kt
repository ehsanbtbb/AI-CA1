fun main(args: Array<String>) {
    val graph = Graph.createUnidirectionalUnweightedGraph("nodes.txt", "edges.txt")
    println(graph.toString())
    graph.bfs(0, 4)
    graph.clear()
    graph.id(0, 4)
    graph.clear()
    graph.ucs(0, 4)
}