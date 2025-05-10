package org.example

import java.util.*

/**
 * A search node for representing paths in the search tree.
 *
 * @param state the puzzle at this node
 * @param parent the parent node, i.e. where this current node comes from
 * @param action the action that led to this node
 * @param cost the total cost to reach this node
 * @param depth the depth in the search tree
 */
data class Node(
    val state: SlidingPuzzle,
    val parent: Node? = null,
    val action: String? = null,
    val cost: Int = 0,
    val depth: Int = 0
)

/**
 * Generic search algorithm.
 *
 * @param initial the starting puzzle
 * @param queuingFunction based on this function, we have Uniform Cost Search and other algorithms
 * @return the goal node if found, else null
 */
fun search(
    initial: SlidingPuzzle,
    queuingFunction: (PriorityQueue<Node>, List<Node>) -> PriorityQueue<Node>
): Node? {
    var frontier = PriorityQueue<Node>(compareBy { it.cost })
    frontier.add(Node(initial))

    while (frontier.isNotEmpty()) {
        val node = frontier.poll()
        if (node.state.isGoal()) return node

        val children = node.state.expand().map {
            Node(it, node, cost = node.cost + 1, depth = node.depth + 1)
        }

        frontier = queuingFunction(frontier, children)
    }

    return null
}

/**
 * A queuing function generator for A* Search based on a given heuristic.
 *
 * The returned function merges the current frontier with new nodes, assigning
 * cost = g(n) + h(n) to each node.
 *
 * @param heuristic a function estimating remaining cost from a puzzle state
 * @return a queuing function for A* search
 */
fun makeAStarQueue(heuristic: (SlidingPuzzle) -> Int): (
    PriorityQueue<Node>, List<Node>
) -> PriorityQueue<Node> = { queue, children ->
    val updated = children.map {
        it.copy(cost = it.cost + heuristic(it.state))
    }
    PriorityQueue(compareBy<Node> { it.cost }).apply {
        addAll(queue)
        addAll(updated)
    }
}

/**
 * A queuing function for Uniform Cost Search.
 *
 * This function merges the frontier and children, prioritizing nodes by cost (g(n)).
 *
 * @param queue the current priority queue (frontier)
 * @param children the list of expanded nodes
 * @return a new priority queue containing both old and new nodes
 */
fun uniformCostQueue(
    queue: PriorityQueue<Node>,
    children: List<Node>
): PriorityQueue<Node> =
    PriorityQueue(compareBy<Node> { it.cost }).apply {
        addAll(queue)
        addAll(children)
    }

fun printSolution(goalNode: Node) {
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

