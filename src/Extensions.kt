import DfsResult.COMPLETE_SEARCH_NO_RESULT
import DfsResult.NO_RESULT_FOUND
import DfsResult.RESULT_FOUND
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

    do {
        currentlyVisitingId = queue.poll()
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
    } while (currentlyVisitingId != endNodeId && queue.isNotEmpty())

    if (currentlyVisitingId == endNodeId) {
        visitedVerticesIds.add(currentlyVisitingId)
        printPath(startNodeId, endNodeId, visitedVerticesIds)
    } else {
        println("No path is found...")
    }
}

fun Graph.ucs(startNodeId: Int, endNodeId: Int) {
    clear()
    val visitedVerticesIds: MutableSet<Int> = mutableSetOf()
    val queue: PriorityQueue<Vertex> = PriorityQueue()
    var currentlyVisitingVertex = data[startNodeId]!!.first
    queue.add(currentlyVisitingVertex)

    do {
        currentlyVisitingVertex = queue.poll()
        visitedVerticesIds.add(currentlyVisitingVertex.id)
        currentlyVisitingVertex.color = BLACK
        val currentlyVisitingVertexNeighbours = data[currentlyVisitingVertex.id]!!.second
        currentlyVisitingVertexNeighbours.forEach {
            val currentNeighborCostValue = currentlyVisitingVertex.costValue + it.first.distanceTo(
                    currentlyVisitingVertex)
            when (it.first.color) {
                WHITE -> {
                    it.first.fatherId = currentlyVisitingVertex.id
                    it.first.costValue = currentNeighborCostValue
                    it.first.color = GRAY
                    queue.add(it.first)
                }
                GRAY -> {
                    if (it.first.costValue > currentNeighborCostValue) {
                        it.first.fatherId = currentlyVisitingVertex.id
                        it.first.costValue = currentNeighborCostValue
                        queue.remove(it.first)
                        queue.add(it.first)
                    }
                }
                BLACK -> {
                    //no-ops
                }
            }
        }
    } while (currentlyVisitingVertex.id != endNodeId && queue.isNotEmpty())

    if (currentlyVisitingVertex.id == endNodeId) {
        printPath(startNodeId, endNodeId, visitedVerticesIds)
    } else {
        println("No path is found...")
    }
}

fun Graph.id(startNodeId: Int, endNodeId: Int) {
    var limitedDepth = 0
    do {
        clear()
        val resultFound = dfs(startNodeId, endNodeId, limitedDepth)
        if (resultFound == COMPLETE_SEARCH_NO_RESULT) {
            println("id has completed... No results found...")
        }
        limitedDepth++
    } while (resultFound == NO_RESULT_FOUND)
}

fun Graph.dfs(startNodeId: Int, endNodeId: Int, limitedDepth: Int): DfsResult {
    val visitedVerticesIds: MutableSet<Int> = mutableSetOf()
    val stack: Stack<Int> = Stack()
    var currentlyVisitingVertex = data[startNodeId]!!
    currentlyVisitingVertex.first.depth = 0
    stack.push(currentlyVisitingVertex.first.id)

    do {
        currentlyVisitingVertex = data[stack.pop()]!!
        visitedVerticesIds.add(currentlyVisitingVertex.first.id)
        currentlyVisitingVertex.first.color = Vertex.Color.BLACK

        currentlyVisitingVertex.takeIf { it.first.depth!! < limitedDepth }?.second?.forEach { currentNeighbour ->
            if (currentNeighbour.first.color == Vertex.Color.WHITE) {
                currentNeighbour.first.depth = currentlyVisitingVertex.first.depth!! + 1
                currentNeighbour.first.fatherId = currentlyVisitingVertex.first.id
                currentNeighbour.first.color = Vertex.Color.GRAY
                stack.push(currentNeighbour.first.id)
            }
        }
    } while (currentlyVisitingVertex.first.id != endNodeId && stack.isNotEmpty())

    return if (currentlyVisitingVertex.first.id == endNodeId) {
        printPath(startNodeId, endNodeId, visitedVerticesIds)
        RESULT_FOUND
    } else {
        println("No path is found... max depth limit: $limitedDepth")
        if (currentlyVisitingVertex.first.depth == limitedDepth) {
            NO_RESULT_FOUND
        } else {
            COMPLETE_SEARCH_NO_RESULT
        }
    }
}

enum class DfsResult {
    COMPLETE_SEARCH_NO_RESULT,
    RESULT_FOUND,
    NO_RESULT_FOUND
}

fun Graph.astar(startNodeId: Int, endNodeId: Int) {
    clear()
    val visitedVerticesIds: MutableSet<Int> = mutableSetOf()
    val queue: PriorityQueue<Vertex> = PriorityQueue()
    var currentlyVisitingVertex = data[startNodeId]!!.first
    queue.add(currentlyVisitingVertex)

    do {
        currentlyVisitingVertex = queue.poll()
        visitedVerticesIds.add(currentlyVisitingVertex.id)
        currentlyVisitingVertex.color = BLACK
        val currentlyVisitingVertexNeighbours = data[currentlyVisitingVertex.id]!!.second
        currentlyVisitingVertexNeighbours.forEach {
            val estimatedCostToDestination = (it.first.distanceTo(
                    data[endNodeId]!!.first) / 120)
            val currentNeighborEstimatedCostValue = currentlyVisitingVertex.realCost + it.second + estimatedCostToDestination
            when (it.first.color) {
                WHITE -> {
                    it.first.fatherId = currentlyVisitingVertex.id
                    it.first.costValue = currentNeighborEstimatedCostValue
                    it.first.realCost = currentNeighborEstimatedCostValue - estimatedCostToDestination
                    it.first.color = GRAY
                    queue.add(it.first)
                }
                GRAY -> {
                    if (it.first.costValue > currentNeighborEstimatedCostValue) {
                        it.first.fatherId = currentlyVisitingVertex.id
                        it.first.costValue = currentNeighborEstimatedCostValue
                        it.first.realCost = currentNeighborEstimatedCostValue - estimatedCostToDestination
                        queue.remove(it.first)
                        queue.add(it.first)
                    }
                }
                BLACK -> {
                    //no-ops
                }
            }
        }
    } while (currentlyVisitingVertex.id != endNodeId && queue.isNotEmpty())

    if (currentlyVisitingVertex.id == endNodeId) {
        printPath(startNodeId, endNodeId, visitedVerticesIds)
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