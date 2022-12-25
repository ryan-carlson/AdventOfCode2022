import utilities.loadResource
import java.io.File

class Operation(operationInput: String) {

    private val operator: String
    private val value1: String
    private val value2: String

    init {
        val inputs = splitOnWhitespace(operationInput)
        value1 = inputs[2]
        operator = inputs[3]
        value2 = inputs[4]
    }

    fun causeWorry(input: Long): Long {
        val valueLeft = when(value1.toLongOrNull())
        {
            null -> input
            else -> value1.toLong()
        }
        val valueRight = when(value2.toLongOrNull())
        {
            null -> input
            else -> value2.toLong()
        }
        return when (operator) {
            "*" ->  {
                Math.multiplyExact(valueLeft, valueRight)
            }
            "+" -> {
                Math.addExact(valueLeft, valueRight)
            }
            else -> {
                throw Error("Unexpected operator for worry operation")
            }
        }
    }
}

class MonkeyClan(val monkeys:List<Monkey>, val manageWorry: (Long)-> Long, val rounds: Int, val verbose:Boolean) {

    fun run(): Long {
        for (i in 1..rounds) {
            this.monkeyBusiness()
        }
        val sorted = this.monkeys.map { monkey -> monkey.inspections }.sortedBy { inspections -> -inspections }
        if (verbose) {
            output()
        }
        return sorted[0] * sorted[1]
    }

    private fun monkeyBusiness() {
        for (monkey in monkeys) {
            while (monkey.hasItem()) {
                monkey.inspect(manageWorry)
                val (item, to) = monkey.throwItem()
                monkeys[to].catch(item)
            }
        }
    }

    private fun output() {
        for ((index, monkey) in monkeys.withIndex()) {
            println("Monkey $index inspected items ${monkey.inspections} times")
        }
    }
}

class MonkeyBrain(
    val divisibleValue: Int,
    private val trueMonkey: Int,
    private val falseMonkey: Int,
) {
    fun throwTo(value: Long): Int {
        return  if (value % divisibleValue == 0L) trueMonkey else falseMonkey
    }
}

class Monkey(
    private val items: MutableList<Long>,
    private val operation: Operation,
    private val brain: MonkeyBrain
){

    var inspections = 0L

    fun hasItem(): Boolean {
        return items.isNotEmpty()
    }

    fun getDivisibleValue(): Int {
        return brain.divisibleValue
    }

    fun inspect(manageWorry: (Long) -> Long) {
        inspections++
        val worry = operation.causeWorry(items.removeAt(0))
        val item =manageWorry(worry)
        items.add(0, item)
    }

    fun throwItem(): Pair<Long, Int> {
        val item = items.removeFirst()
        return Pair(item, brain.throwTo(item))
    }

    fun catch(item: Long) {
        items.add(item)
    }
}

fun load(): List<Monkey> {
    return loadResource("day-11-input").path.let {
        File(it).readLines().fold (mutableListOf(mutableListOf())) { acc: MutableList<MutableList<String>>, item ->
            if(item.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(item)
            }
            acc
        }.map {item: MutableList<String> ->
            Monkey(
                items=item[1].split(":")[1].split(",").map { it -> it.trim().toLong() }.toMutableList(),
                operation=Operation(item[2].split(":")[1]),
                brain=MonkeyBrain(
                    divisibleValue=splitOnWhitespace(item[3]).last().toInt(),
                    trueMonkey=splitOnWhitespace(item[4]).last().toInt(),
                    falseMonkey=splitOnWhitespace(item[5]).last().toInt(),
                )
            )
        }
    }
}

fun main() {
    val result1 = MonkeyClan(monkeys=load(), manageWorry={worry->worry/3},rounds=20, verbose=false).run()
    println("Part One Result: $result1")

    val monkeys = load()
    val smallestDivisor: Int = load().map {monkey -> monkey.getDivisibleValue()}.reduce {a, b -> a*b}
    val result2 = MonkeyClan(monkeys=monkeys, manageWorry={worry->worry%smallestDivisor},rounds=10000, verbose=false).run()
    println("Part Two Result: $result2")
}
