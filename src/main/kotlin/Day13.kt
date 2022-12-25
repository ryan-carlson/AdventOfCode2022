import utilities.loadResource
import java.io.File

//import java.io.File
//
//class PacketCompare(private val packet1: String, private val packet2: String) {
//
//    fun compare(): Boolean {
//        return compare(packet1.substring(1, packet1.length-1), packet2.substring(1, packet2.length-1)) <= 1
//    }
//
//    private fun endOfArrayIndex(value: String): Int {
//        var openArrays = 0
//        for ((index, char) in value.withIndex()) {
//            when (char) {
//                '[' -> openArrays++
//                ']' -> openArrays--
//            }
//            if (openArrays == 0) {
//                return index
//            }
//        }
//        throw Error("Parsing error")
//    }
//
//    private fun compare(value1: String, value2: String): Int {
//        var index1 = 0
//        var index2 = 0
//        while (index1 < value1.length && index2 < value2.length) {
//            if (value1[index1] == ',') {
//                index1++
//            }
//            if (value2[index2] == ',') {
//                index2++
//            }
//            val left = value1[index1]
//            val right = value2[index2]
//            if (left.isDigit() && right.isDigit()) {
//                if (left != right) {
//                    if (left < right) {
//                        return 1
//                    } else {
//                        return 2
//                    }
//                }
//            } else if (!left.isDigit() && !right.isDigit()) {
//                val leftEndOfArrayIndex = endOfArrayIndex(value1.substring(index1))+index1
//                val rightEndOfArrayIndex = endOfArrayIndex(value2.substring(index2))+index2
//                val leftArray = value1.substring(index1+1, leftEndOfArrayIndex)
//                val rightArray = value2.substring(index2+1, rightEndOfArrayIndex)
//                val comparison = compare(leftArray, rightArray)
//                if (comparison != 0) {
//                    return comparison
//                }
//                index1 = leftEndOfArrayIndex
//                index2 = rightEndOfArrayIndex
//            } else if (left.isDigit()) {
//                val rightEndOfArrayIndex = endOfArrayIndex(value2.substring(index2))+index2
//                val leftArray = left.toString()
//                val rightArray = value2.substring(index2+1, rightEndOfArrayIndex)
//                val comparison = compare(leftArray, rightArray)
//                if (comparison != 0) {
//                    return comparison
//                }
//                index2 = rightEndOfArrayIndex
//            } else {
//                val leftEndOfArrayIndex = endOfArrayIndex(value1.substring(index1))+index1
//                val leftArray = value1.substring(index1+1, leftEndOfArrayIndex)
//                val rightArray = right.toString()
//                val comparison = compare(leftArray, rightArray)
//                if (comparison != 0) {
//                    return comparison
//                }
//                index1 = leftEndOfArrayIndex
//            }
//            index1++
//            index2++
//        }
//        if (index1 == value1.length && index2 == value2.length) {
//            return 0 // Same!!
//        } else if (index1 == value1.length) {
//            return 1
//        } else {
//            return 2
//        }
//    }
//}
//
fun main() {
    val inputs = loadResource("day-13-input")?.path?.let {
        File(it).readLines()
    }!!
//    val inputs = loadResource("day-13-input")?.path?.let {
//        File(it).readLines().chunked(3).map { group -> Pair(group[0], group[1]) }
//    }!!
//    var result = 0
//    for ((index, input) in inputs.withIndex()) {
//        if (PacketCompare(input.first, input.second).compare()) {
//            result += index+1
//        }
//    }
//    println("Result for Part One: $result")
    println(Day13(inputs).solvePart1())
}

class Day13(input: List<String>) {

    private val packets = input.filter { it.isNotBlank() }.map { Packet.of(it) }

    fun solvePart1(): Int =
        packets.chunked(2).mapIndexed { index, pair ->
            if (pair.first() < pair.last()) index + 1 else 0
        }.sum()

    fun solvePart2(): Int {
        val dividerPacket1 = Packet.of("[[2]]")
        val dividerPacket2 = Packet.of("[[6]]")
        val ordered = (packets + dividerPacket1 + dividerPacket2).sorted()
        return (ordered.indexOf(dividerPacket1) + 1) * (ordered.indexOf(dividerPacket2) + 1)
    }

    private sealed class Packet : Comparable<Packet> {
        companion object {
            fun of(input: String): Packet =
                of(
                    input.split("""((?<=[\[\],])|(?=[\[\],]))""".toRegex())
                        .filter { it.isNotBlank() }
                        .filter { it != "," }
                        .iterator()
                )

            private fun of(input: Iterator<String>): Packet {
                val packets = mutableListOf<Packet>()
                while (input.hasNext()) {
                    when (val symbol = input.next()) {
                        "]" -> return ListPacket(packets)
                        "[" -> packets.add(of(input))
                        else -> packets.add(IntPacket(symbol.toInt()))
                    }
                }
                return ListPacket(packets)
            }
        }
    }

    private class IntPacket(val amount: Int) : Packet() {
        fun asList(): Packet = ListPacket(listOf(this))

        override fun compareTo(other: Packet): Int =
            when (other) {
                is IntPacket -> amount.compareTo(other.amount)
                is ListPacket -> asList().compareTo(other)
            }
    }

    private class ListPacket(val subPackets: List<Packet>) : Packet() {
        override fun compareTo(other: Packet): Int =
            when (other) {
                is IntPacket -> compareTo(other.asList())
                is ListPacket -> subPackets.zip(other.subPackets)
                    .map { it.first.compareTo(it.second) }
                    .firstOrNull { it != 0 } ?: subPackets.size.compareTo(other.subPackets.size)
            }
    }
}