import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import structures.Path

class Day16Test {

    private val aa = ValveVertex("AA", Valve("AA", 0, listOf("DD", "II", "BB")))
    private val bb = ValveVertex("BB", Valve("BB", 13, listOf("CC", "AA")))
    private val cc = ValveVertex("CC", Valve("CC", 2, listOf("DD", "BB")))
    private val dd = ValveVertex("DD", Valve("DD", 20, listOf("CC", "AA", "EE")))
    private val ee = ValveVertex("EE", Valve("EE", 3, listOf("FF", "DD")))
    private val ff = ValveVertex("FF", Valve("FF", 0, listOf("EE", "GG")))
    private val gg = ValveVertex("GG", Valve("GG", 0, listOf("FF", "HH")))
    private val hh = ValveVertex("HH", Valve("HH", 22, listOf("GG")))
    private val ii = ValveVertex("II", Valve("II", 0, listOf("AA", "JJ")))
    private val jj = ValveVertex("JJ", Valve("JJ", 21, listOf("II")))

//    @Test
//    fun testTotalPressure() {
//        var path = newValvePath(aa)
//        path = path.traverse(Path(listOf(aa, dd)))
//        path = path.traverse(Path(listOf(dd, cc, bb)))
//        path = path.traverse(Path(listOf(bb, aa, ii, jj)))
//        path = path.traverse(Path(listOf(jj, ii, aa, dd, ee, ff, gg, hh)))
//        path = path.traverse(Path(listOf(hh, gg, ff, ee)))
//        path = path.traverse(Path(listOf(ee, dd, cc)))
//        Assertions.assertEquals(1651,  path.totalPressure())
//    }

    @Test
    fun testSamplePart1() {
        val lines = listOf(
            "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB",
            "Valve BB has flow rate=13; tunnels lead to valves CC, AA",
            "Valve CC has flow rate=2; tunnels lead to valves DD, BB",
            "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE",
            "Valve EE has flow rate=3; tunnels lead to valves FF, DD",
            "Valve FF has flow rate=0; tunnels lead to valves EE, GG",
            "Valve GG has flow rate=0; tunnels lead to valves FF, HH",
            "Valve HH has flow rate=22; tunnel leads to valve GG",
            "Valve II has flow rate=0; tunnels lead to valves AA, JJ",
            "Valve JJ has flow rate=21; tunnel leads to valve II",
        )
        Assertions.assertEquals(1651, PressureLocator(lines, 30).findOptimalPathPressure())
    }

//    @Test
//    fun testSamplePart2() {
//        val lines = listOf(
//            "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB",
//            "Valve BB has flow rate=13; tunnels lead to valves CC, AA",
//            "Valve CC has flow rate=2; tunnels lead to valves DD, BB",
//            "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE",
//            "Valve EE has flow rate=3; tunnels lead to valves FF, DD",
//            "Valve FF has flow rate=0; tunnels lead to valves EE, GG",
//            "Valve GG has flow rate=0; tunnels lead to valves FF, HH",
//            "Valve HH has flow rate=22; tunnel leads to valve GG",
//            "Valve II has flow rate=0; tunnels lead to valves AA, JJ",
//            "Valve JJ has flow rate=21; tunnel leads to valve II",
//        )
//        val result = PressureLocator(lines, 30).findOptimalPath()!!
//        Assertions.assertEquals(1707,  result.totalPressure())
//    }
}