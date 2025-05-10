package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val puzzle = SlidingPuzzle(size = 3)
    puzzle.scramble(steps = 20)

    println("Initial puzzle:")
    puzzle.print()

    val heuristic = { p: SlidingPuzzle -> p.manhattan() }
    val goalNode = search(puzzle, makeAStarQueue(heuristic))

    if (goalNode == null) {
        println("No solution found.")
    } else {
        println("Solution found in ${goalNode.depth} steps.")
        val path = mutableListOf<Node>()
        var current: Node? = goalNode
        while (current != null) {
            path.add(current)
            current = current.parent
        }
        path.reversed().forEach { node ->
            node.action?.let { println("Move: $it") }
            node.state.print()
        }
    }
}

