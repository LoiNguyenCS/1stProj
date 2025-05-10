package org.example


/**
 * A simple data class representing the position of a tile in the puzzle grid.
 *
 * @param row the row index
 * @param col the column index
 */
data class Position(val row: Int, val col: Int)


/**
 * This class represents a sliding puzzle with its own state that could be updated.
 *
 * @param size the dimension of the puzzle grid (e.g., 3 for 3x3)
 */
class SlidingPuzzle(val size: Int) {

    /**
     * This is the state of the sliding puzzle. We set the initial state to be the
     * goal state.
     */
    private var state: MutableMap<Position, Int> = buildGoalState().toMutableMap()

    /**
     * A function to flexibly set the state of the sliding puzzle.
     */
    fun setState(state: MutableMap<Position, Int>) {
        this.state = state
    }

    /**
     * Returns the current puzzle state as a map of tile positions to values.
     */
    fun currentState(): Map<Position, Int> = state.toMap()


    /**
     * Scrambles the puzzle by applying random valid moves.
     *
     * @param steps the number of random moves, set to 10 by default.
     */
    fun scramble(steps: Int = 10) {
        val directions = listOf("UP", "DOWN", "LEFT", "RIGHT")
        repeat(steps) {
            directions.shuffled().firstOrNull { this.move(it) }
        }
    }

    /**
     * Applies a move in the specified direction by sliding the blank tile (0).
     *
     * In real life, we can only move the non-blank tiles. But for the ease of
     * programming, we only move the blank tile. These two options should be equivalent.
     *
     * @param direction the direction to move ("UP", "DOWN", "LEFT", "RIGHT")
     * @return true if the move was valid and applied, false otherwise
     */
    fun move(direction: String): Boolean {
        val blank = state.entries.first { it.value == 0 }.key
        val (dr, dc) = when (direction) {
            "UP" -> -1 to 0
            "DOWN" -> 1 to 0
            "LEFT" -> 0 to -1
            "RIGHT" -> 0 to 1
            else -> throw  IllegalArgumentException("Invalid direction")
        }
        val newPos = Position(blank.row + dr, blank.col + dc)
        if (newPos !in stateBounds()) return false

        // This is not necessary, but Kotlin type system forces us to do a nullability
        // check for nullness safety.
        val tile = state[newPos] ?: return false

        state[blank] = tile
        state[newPos] = 0
        return true
    }

    /**
     * Returns a deep copy of this puzzle.
     */
    fun copy(): SlidingPuzzle {
        val newPuzzle = SlidingPuzzle(size)
        newPuzzle.state.clear()
        newPuzzle.state.putAll(this.state)
        return newPuzzle
    }

    /**
     * Generates all valid next states from the current puzzle.
     *
     * @return list of new SlidingPuzzle instances for each valid move
     */
    fun expand(): List<SlidingPuzzle> {
        val directions = listOf("UP", "DOWN", "LEFT", "RIGHT")
        return directions.mapNotNull { dir ->
            val clone = copy()
            if (clone.move(dir)) clone else null
        }
    }

    /**
     * Checks whether the puzzle is in goal configuration.
     */
    fun isGoal(): Boolean = state == buildGoalState()

    /**
     * Returns the current Manhattan distance of the puzzle.
     */
    fun manhattan(): Int = state.entries.sumOf { (pos, value) ->
        if (value == 0) 0 else {
            val goalRow = (value - 1) / size
            val goalCol = (value - 1) % size
            Math.abs(pos.row - goalRow) + Math.abs(pos.col - goalCol)
        }
    }

    /**
     * Returns the number of misplaced tiles (excluding the blank).
     */
    fun misplaced(): Int = state.count { (pos, value) ->
        value != 0 && value != buildGoalState()[pos]
    }

    /**
     * Prints the puzzle to the console.
     */
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
     * Builds the goal state for the puzzle. The goal state is where every
     * tile is ordered correctly.
     */
    private fun buildGoalState(): Map<Position, Int> {
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

    /**
     * Returns the set of all valid positions within the puzzle grid,
     * based on the current puzzle size.
     *
     * @return a set of all positions from (0,0) to (size-1, size-1)
     */
    private fun stateBounds(): Set<Position> =
        (0 until size).flatMap { r -> (0 until size).map { c -> Position(r, c) } }.toSet()
}
