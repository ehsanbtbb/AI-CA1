data class Vertex constructor(val id: Int, val latitude: Float,
        val longitude: Float) : Comparable<Vertex> {
    var fatherId: Int? = null
    var color: Color = Color.WHITE
    var depth: Int? = null
    var realCost: Float = 0f
    var estimatedCost: Float = 0f
    var costValue: Float = 0f

    override fun toString(): String {
        return "[id: $id, latitude: $latitude, longitude: $longitude, color: $color, depth: $depth]"
    }

    override fun compareTo(other: Vertex): Int {
        return costValue.compareTo(other.costValue)
    }

    enum class Color {
        /**
         * This indicates that the vertex is neither visited, nor in the queue for being visited.
         */
        WHITE,
        /**
         * This indicates that the vertex is in the queue for being visited.
         */
        GRAY,
        /**
         * This indicates that the vertex is visited.
         */
        BLACK
    }
}

data class Edge constructor(val nodeOneId: Int, val nodeTwoId: Int, val maxSpeed: Int,
        val isOneWay: Boolean)