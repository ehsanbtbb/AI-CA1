fun main(args: Array<String>) {
    val graph1 = Graph.createUnidirectionalUnweightedGraph("nodes.txt", "edges.txt")
//    graph1.bfs()
//    graph1.id()
//    graph1.ucs()

    val graph2 = Graph.createBidirectionalWeightedGraph("nodes.txt", "edges.txt")
//    graph2.astar()
}