import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Day15Test {
    @Test
    fun insertToEmpty() {
        assertEquals(listOf(1..2), insert(listOf(), 1..2))
    }

    @Test
    fun insertAtEnd() {
        assertEquals(listOf(1..2, 10..20), insert(listOf(1..2), 10..20))
    }

    @Test
    fun insertAtStart() {
        assertEquals(listOf(1..2, 10..20), insert(listOf(10..20), 1..2))
    }

    @Test
    fun insertInMiddle() {
        assertEquals(listOf(10..20, 25..26, 30..40), insert(listOf(10..20, 30..40), 25..26))
    }

    @Test
    fun envelopOne() {
        assertEquals(listOf(9..21, 30..40), insert(listOf(10..20, 30..40), 9..21))
    }
}