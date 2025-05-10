package org.example

import kotlin.system.measureTimeMillis

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val puzzle = SlidingPuzzle(size = 3)
    puzzle.scramble(steps = 30)

    println("Initial puzzle:")
    puzzle.print()

    val heuristicForMahattan = { p: SlidingPuzzle -> p.manhattan() }
    val heuristicForMisplaced = { p: SlidingPuzzle -> p.misplaced() }

    runSearch("A* Search (Manhattan)") {
        search(puzzle, makeAStarQueue(heuristicForMahattan))
    }

    runSearch("A* Search (Misplaced Tiles)") {
        search(puzzle, makeAStarQueue(heuristicForMisplaced))
    }

    runSearch("Uniform Cost Search") {
        search(puzzle, ::uniformCostQueue)
    }

}

fun runSearch(
    name: String,
    searchFn: () -> Node?
) {
    println("Running $name")
    var result: Node? = null
    val time = measureTimeMillis {
        result = searchFn()
    }
    println("$name Time: $time ms")

    if (result == null) {
        println("$name: No solution found.")
    } else {
        printSolution(result!!)
    }
}



