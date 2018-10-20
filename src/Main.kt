fun main(args: Array<String>) {
    val graph = Graph.createUnidirectionalUnweightedGraph("nodes0.txt", "edges0.txt")
    println(graph.toString())
    graph.bfs(0, 2)
    graph.bfs(2, 0)
    graph.bfs(4, 1)
    graph.bfs(1, 3)
//    graph.clear()
//    graph.id(0, 4)
//    graph.clear()
//    graph.ucs(0, 4)
}