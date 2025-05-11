package org.example

import kotlin.system.measureTimeMillis

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val puzzle = createInitialPuzzle(3)

    println("Initial puzzle:")
    puzzle.print()

    runSearch("A* Search (Manhattan)") {
        search(
            puzzle,
            makeAStarQueue {
                p: SlidingPuzzle -> p.manhattan()
            }
        )
    }

    runSearch("A* Search (Misplaced Tiles)") {
        search(
            puzzle,
            makeAStarQueue {
                    p: SlidingPuzzle -> p.misplaced()
            }
        )
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

fun createInitialPuzzle(size: Int = 3): SlidingPuzzle {
    val puzzle = SlidingPuzzle(size)

    println("Do you want to enter a custom puzzle state? (y/n):")
    val input = readln().trim().lowercase()

    if (input == "y") {
        println("Enter ${size * size} numbers (0 for blank), separated by space:")
        val tiles = readln().trim().split(Regex("\\s+")).mapNotNull { it.toIntOrNull() }

        if (tiles.size != size * size) {
            println("Invalid input: Expected ${size * size} numbers. Using default scramble.")
            return puzzle.scramble(steps = 30)
        } else {
            return puzzle.setState(tiles)
        }
    } else {
        return puzzle.scramble(steps = 30)
    }
}



