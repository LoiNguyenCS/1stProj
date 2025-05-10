package org.example

import kotlin.system.measureTimeMillis

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val puzzle = SlidingPuzzle(size = 3)
    puzzle.scramble(steps = 20)

    println("Initial puzzle:")
    puzzle.print()

    val heuristic = { p: SlidingPuzzle -> p.manhattan() }

    var goalNode: Node?
    val timeTaken = measureTimeMillis {
        goalNode = search(puzzle, makeAStarQueue(heuristic))
    }

    println("Time: $timeTaken ms")

    if (goalNode == null) {
        println("No solution found.")
    } else {
        printSolution(goalNode!!)
    }
}

