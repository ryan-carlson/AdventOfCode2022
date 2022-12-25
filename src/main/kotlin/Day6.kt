import utilities.loadResource
import java.io.File
import java.util.*

const val START_OF_PACKET_LENGTH = 14

fun findStartOfPacket(value: String): Int? {
    val values: Queue<Char> = LinkedList()
    for ((streamCounter, char) in value.withIndex()) {
        values.add(char)
        if (values.size > START_OF_PACKET_LENGTH) {
            values.poll()
        }
        if (values.toSet().size == START_OF_PACKET_LENGTH) {
            return streamCounter
        }
    }
    return null
}

fun main() {
    File(loadResource("day-6-input").path).forEachLine {
        val result = findStartOfPacket(it)!! + 1
        println("Result Part 1: $result")
    }
}