package org.example

/**
 * A simple data class representing the position of a tile in the puzzle grid.
 */
data class Position(val row: Int, val col: Int)

/**
 * An immutable representation of a sliding puzzle.
 *
 * @param size the puzzle grid dimension (e.g., 3 for 3x3)
 * @param state the mapping from grid positions to tile values, set to goal state by default
 */
class SlidingPuzzle(val size: Int, val state: Map<Position, Int> = buildGoalState(size)) {

    /**
     * Applies a move and returns a new puzzle state if valid, else null.
     *
     * @param direction the direction to move ("UP", "DOWN", "LEFT", "RIGHT")
     * @return a new SlidingPuzzle if the move is valid, null otherwise
     */
    fun move(direction: String): SlidingPuzzle? {
        val blank = state.entries.first { it.value == 0 }.key
        val (dr, dc) = when (direction) {
            "UP" -> -1 to 0
            "DOWN" -> 1 to 0
            "LEFT" -> 0 to -1
            "RIGHT" -> 0 to 1
            else -> throw IllegalArgumentException("Invalid direction")
        }

        val newPos = Position(blank.row + dr, blank.col + dc)
        if (newPos !in stateBounds()) return null

        val tile = state[newPos] ?: return null

        val newState = state.toMutableMap().apply {
            this[blank] = tile
            this[newPos] = 0
        }

        return SlidingPuzzle(size, newState)
    }

    /**
     * Returns a new puzzle after applying a number of random moves
     *
     * @param steps number of random moves to apply
     */
    fun scramble(steps: Int = 10): SlidingPuzzle {
        val directions = listOf("UP", "DOWN", "LEFT", "RIGHT")
        var result = this
        repeat(steps) {
            val next = directions.shuffled().firstNotNullOfOrNull { dir -> result.move(dir) }
            if (next != null) result = next
        }
        return result
    }

    /**
     * Generates all valid next states.
     */
    fun expand(): List<SlidingPuzzle> =
        listOf("UP", "DOWN", "LEFT", "RIGHT").mapNotNull { move(it) }

    /**
     * Returns true if this puzzle is in the goal state.
     */
    fun isGoal(): Boolean = state == buildGoalState(size)

    /**
     * Returns the Manhattan distance.
     */
    fun manhattan(): Int = state.entries.sumOf { (pos, value) ->
        if (value == 0) 0 else {
            val goalRow = (value - 1) / size
            val goalCol = (value - 1) % size
            Math.abs(pos.row - goalRow) + Math.abs(pos.col - goalCol)
        }
    }

    /**
     * Returns the number of misplaced tiles.
     */
    fun misplaced(): Int = state.count { (pos, value) ->
        value != 0 && value != buildGoalState(size)[pos]
    }

    fun print() {
        for (r in 0 until size) {
            for (c in 0 until size) {
                val value = state[Position(r, c)]
                print(String.format("%2s ", if (value == 0) " " else value.toString()))
            }
            println()
        }
        println("-----------")
    }

    /**
     * Returns a new puzzle with custom tile values.
     */
    fun setState(flat: List<Int>): SlidingPuzzle {
        if (flat.size != size * size) {
            throw IllegalArgumentException("Expected ${size * size} values, got ${flat.size}")
        }

        val newState = flat.mapIndexed { index, value ->
            val row = index / size
            val col = index % size
            Position(row, col) to value
        }.toMap()

        return SlidingPuzzle(size, newState)
    }

    private fun stateBounds(): Set<Position> =
        (0 until size).flatMap { r -> (0 until size).map { c -> Position(r, c) } }.toSet()

    companion object {
        /**
         * Return the goal state for the sliding puzzle problem, i.e. where all tiles
         * are placed orderly.
         *
         * @param size the size of the sliding puzzle.
         * @return the goal state of the puzzle in the form of a map, where each grid
         * position is mapped to a corresponding tile value.
         */
        fun buildGoalState(size: Int): Map<Position, Int> {
            val entries = mutableMapOf<Position, Int>()
            var count = 1
            for (row in 0 until size) {
                for (col in 0 until size) {
                    val value = if (row == size - 1 && col == size - 1) 0 else count++
                    entries[Position(row, col)] = value
                }
            }
            return entries
        }
    }
}
