import Vertex.Color.BLACK
import Vertex.Color.GRAY
import Vertex.Color.WHITE
import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.Queue
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

fun Vertex.distanceTo(vertex: Vertex): Float {
    return sqrt((latitude - vertex.latitude).pow(2).plus((longitude - vertex.longitude).pow(2)))
}

fun Vertex.clear() {
    fatherId = null
    depth = null
    color = Vertex.Color.WHITE
}

fun Graph.clear() {
    data.forEach {
        it.value.first.clear()
    }
}

fun Graph.bfs(startNodeId: Int, endNodeId: Int) {
    clear()
    val visitedVerticesIds: MutableSet<Int> = mutableSetOf()
    val queue: Queue<Int> = ArrayDeque<Int>()
    var currentlyVisitingId = startNodeId
    queue.add(currentlyVisitingId)

    while (currentlyVisitingId != endNodeId && queue.isNotEmpty()) {
        visitedVerticesIds.add(currentlyVisitingId)
        val currentlyVisitingVertex = data[currentlyVisitingId]?.first!!
        currentlyVisitingVertex.color = BLACK

        val currentlyVisitingVertexNeighbours = data[currentlyVisitingId]!!.second
        currentlyVisitingVertexNeighbours.forEach {
            if (it.first.color == WHITE) {
                queue.add(it.first.id)
                it.first.color = GRAY
                it.first.fatherId = currentlyVisitingId
            }
        }
        currentlyVisitingId = queue.poll()
    }
    if (currentlyVisitingId == endNodeId) {
        visitedVerticesIds.add(currentlyVisitingId)
        printPath(startNodeId, endNodeId, visitedVerticesIds)
    } else {
        println("No path is found...")
    }
}

fun Graph.id(startNodeId: Int, endNodeId: Int) {
    var limitedDepth = 0
    while (!dfs(startNodeId, endNodeId, limitedDepth)) {
        limitedDepth++
        clear()
    }
}

fun Graph.dfs(startNodeId: Int, endNodeId: Int, limitedDepth: Int): Boolean {
    val visitedNodes: MutableSet<Int> = mutableSetOf()
    val stack: Stack<Int> = Stack()
    stack.push(startNodeId)
    data[startNodeId]?.first?.depth = 0
    var currentNode = data[startNodeId]

    while (currentNode?.first?.id != endNodeId && stack.isNotEmpty()) {
        currentNode = data[stack.pop()]
        visitedNodes.add(currentNode?.first?.id!!)
        currentNode.first.color = Vertex.Color.BLACK

        currentNode.takeIf { it.first.depth!! < limitedDepth }?.second?.forEach {
            if (it.first.color == Vertex.Color.WHITE) {
                it.first.depth = currentNode.first.depth!! + 1
                it.first.fatherId = currentNode.first.id
                it.first.color = Vertex.Color.GRAY
                stack.push(it.first.id)
            }
        }
    }
    return if (currentNode?.first?.id!! == endNodeId) {
        printPath(startNodeId, endNodeId, visitedNodes)
        true
    } else {
        println("No path is found... max depth limit: $limitedDepth")
        false
    }
}

//fun Graph.ucs(startNodeId: Int, endNodeId: Int) {
//    val visitedNodes: MutableSet<Int> = mutableSetOf()
//    val queue: PriorityQueue<Vertex> = PriorityQueue()
//    queue.add(data[startNodeId]?.first!!)
//    var currentNodeId = startNodeId
//
//    while (currentNodeId != endNodeId && queue.isNotEmpty()) {
//        val headOfQueue = queue.poll()
//        visitedNodes.add(headOfQueue.id)
//        headOfQueue.isVisited = true
//        currentNodeId = headOfQueue.id
//        data[currentNodeId]?.second?.forEach {
//            if (!it.first.isVisited) {
//                it.first.fatherId = currentNodeId
//                it.first.costValue = data[currentNodeId]?.first?.costValue!! + data[currentNodeId]?.first?.distanceTo(
//                        it.first)!!
//                queue.add(it.first)
//            }
//        }
//    }
//    if (currentNodeId == endNodeId) {
//        printPath(startNodeId, endNodeId, visitedNodes)
//    } else {
//        println("No path is found...")
//    }
//}

fun Graph.astart(startNodeId: Int, endNodeId: Int) {
    val visitedNodes: MutableSet<Int> = mutableSetOf()
    val queue: PriorityQueue<Vertex> = PriorityQueue()
    queue.add(data[startNodeId]?.first!!)
    var currentNodeId = startNodeId

    while (currentNodeId != endNodeId && queue.isNotEmpty()) {
        val headOfQueue = queue.poll()
        visitedNodes.add(headOfQueue.id)
        headOfQueue.color = Vertex.Color.BLACK
        currentNodeId = headOfQueue.id

        data[currentNodeId]?.second?.forEach {
            val newCost = data[currentNodeId]?.first?.realCost!! + it.second + it.first.distanceTo(
                    data[endNodeId]?.first!!) / 120
            if (it.first.color != Vertex.Color.BLACK && newCost < it.first.estimatedCost) {
                it.first.color = Vertex.Color.GRAY
                it.first.fatherId = currentNodeId
                it.first.costValue = newCost
                queue.add(it.first)
            }
        }
    }
    if (currentNodeId == endNodeId) {
        printPath(startNodeId, endNodeId, visitedNodes)
    } else {
        println("No path is found...")
    }
}

fun Graph.printPath(startNodeId: Int, endNodeId: Int, visitedNodes: Set<Int>) {
    println("visited: ${visitedNodes.size}")
    val pathIds: ArrayList<Int> = arrayListOf(endNodeId)
    var currentPathElement = endNodeId
    var distance = 0f
    while (currentPathElement != startNodeId) {
        data[currentPathElement]?.first?.let {
            currentPathElement = it.fatherId!!
            distance += it.distanceTo(data[it.fatherId!!]?.first!!)
        }
        pathIds.add(currentPathElement)
    }
    println("path: ${pathIds.size}")
    println("distance: $distance")
    println("${pathIds.reversed()}")
}