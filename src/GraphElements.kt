data class Vertex constructor(val id: Int, val latitude: Float,
        val longitude: Float) : Comparable<Vertex> {
    var fatherId: Int? = null
    var isVisited: Boolean = false
    var color: Color = Color.WHITE
    var depth: Int? = null
    var realCost: Float = 0f
    var estimatedCost: Float = 0f
    var costValue: Float = 0f

    override fun toString(): String {
        return "[id: $id, latitude: $latitude, longitude: $longitude, isVisited: $isVisited, depth: $depth]"
    }

    override fun compareTo(other: Vertex): Int {
        return other.costValue.compareTo(costValue)
    }

    enum class Color {
        WHITE, GRAY, BLACK
    }
}

data class Edge constructor(val nodeOneId: Int, val nodeTwoId: Int, val maxSpeed: Int,
        val isOneWay: Boolean)