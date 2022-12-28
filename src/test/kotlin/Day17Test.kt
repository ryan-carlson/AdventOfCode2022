import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

const val jets = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

class VerticalCaveTest {
    @Test
    fun construct() {
        Assertions.assertEquals(0, VerticalCave(jets).dropRocks(0))
    }

    @Test
    fun drop1Rock() {
        Assertions.assertEquals(1, VerticalCave(jets).dropRocks(1))
    }

    @Test
    fun drop2Rocks() {
        Assertions.assertEquals(4, VerticalCave(jets).dropRocks(2))
    }

    @Test
    fun drop3Rocks() {
        Assertions.assertEquals(6, VerticalCave(jets).dropRocks(3))
    }

    @Test
    fun drop4Rocks() {
        Assertions.assertEquals(7, VerticalCave(jets).dropRocks(4))
    }

    @Test
    fun drop5Rocks() {
        Assertions.assertEquals(9, VerticalCave(jets).dropRocks(5))
    }

    @Test
    fun drop10Rocks() {
        Assertions.assertEquals(17, VerticalCave(jets).dropRocks(10))
    }
}

class Day17Test {
    @Test
    fun testSamplePart1() {
        Assertions.assertEquals(3068, solveDay17Part1(listOf(jets), 10))
    }
}