import java.io.File

class Forest(private val forest: Array<IntArray>) {

    private fun visibleFromTop(x: Int, y: Int): Boolean {
        val treeHeight = forest[y][x]
        for (i in 0 until y) {
            if (forest[i][x] >= treeHeight) {
                return false
            }
        }
        return true
    }

    private fun visibleFromBottom(x: Int, y: Int): Boolean {
        val treeHeight = forest[y][x]
        for (i in forest.size-1 downTo  y+1) {
            if (forest[i][x] >= treeHeight) {
                return false
            }
        }
        return true
    }

    private fun visibleFromLeft(x: Int, y: Int): Boolean {
        val treeHeight = forest[y][x]
        for (i in 0 until x) {
            if (forest[y][i] >= treeHeight) {
                return false
            }
        }
        return true
    }

    private fun visibleFromRight(x: Int, y: Int): Boolean {
        val treeHeight = forest[y][x]
        for (i in forest[y].size-1 downTo x+1) {
            if (forest[y][i] >= treeHeight) {
                return false
            }
        }
        return true
    }

    private fun visible(x: Int, y: Int): Boolean {
        return visibleFromTop(x, y) ||
                visibleFromLeft(x, y) ||
                visibleFromBottom(x, y) ||
                visibleFromRight(x, y)
    }

    fun findVisibleTrees():Int {
        var visibleCount = 0
        for ((y, treeLine) in forest.withIndex()) {
            for ((x, _) in treeLine.withIndex()) {
                if (visible(x, y)) {
                    visibleCount += 1
                }
            }
        }
        return visibleCount
    }

    private fun visibleTreesLeft(x: Int, y: Int): Int {
        val treeHeight = forest[y][x]
        var visibleTrees = 0
        for (i in x-1 downTo 0) {
            visibleTrees += 1
            if (forest[y][i] >= treeHeight) {
                break
            }
        }
        return visibleTrees
    }

    private fun visibleTreesRight(x: Int, y: Int): Int {
        val treeHeight = forest[y][x]
        var visibleTrees = 0
        for (i in x+1 until  forest[y].size) {
            visibleTrees += 1
            if (forest[y][i] >= treeHeight) {
                break
            }
        }
        return visibleTrees
    }

    private fun visibleTreesTop(x: Int, y: Int): Int {
        val treeHeight = forest[y][x]
        var visibleTrees = 0
        for (i in y-1 downTo 0) {
            visibleTrees += 1
            if (forest[i][x] >= treeHeight) {
                break
            }
        }
        return visibleTrees
    }

    private fun visibleTreesBottom(x: Int, y: Int): Int {
        val treeHeight = forest[y][x]
        var visibleTrees = 0
        for (i in y+1 until forest.size) {
            visibleTrees += 1
            if (forest[i][x] >= treeHeight) {
                break
            }
        }
        return visibleTrees
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun calculateScenicScore(x: Int, y: Int): Int {
        return visibleTreesLeft(x, y) * visibleTreesRight(x, y) * visibleTreesTop(x, y) * visibleTreesBottom(x, y)
    }

    fun calculateMaxScenicScore():Int {
        var max = 0
        for ((y, treeLine) in forest.withIndex()) {
            for ((x, _) in treeLine.withIndex()) {
                val score = calculateScenicScore(x, y)
                if (score > max) {
                    max = score
                }
            }
        }
        return max
    }
}

fun loadForest(): Forest {
    var forest: Array<IntArray> = arrayOf()
    loadResource("day-8-input")?.path?.let {
        File(it).forEachLine { line ->
            val trees = line.map { c -> c.digitToInt() }.toIntArray()
            forest += trees
        }
    }
    return Forest(forest)
}

fun main() {
    val forest = loadForest()
    val visibleTrees = forest.findVisibleTrees()
    println("Part 1 result: $visibleTrees")

    val maxScenicScore = forest.calculateMaxScenicScore()
    println("Part 2 result: $maxScenicScore")
}