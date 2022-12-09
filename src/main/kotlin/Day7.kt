import java.io.File

class NamedFile(private val name: String, val size: Int){
    override fun toString(): String {
        return "NamedFile(name='$name', size=$size)"
    }
}

class Directory{
    var parent:Directory? = null

    private var children:MutableList<Directory> = mutableListOf()
    private var files:MutableList<NamedFile> = mutableListOf()

    fun addChild(node:Directory){
        children.add(node)
        node.parent = this
    }

    fun addFile(file:NamedFile){
        files.add(file)
    }

    fun find(search: (dir: Directory) -> Boolean): List<Directory> {
        val result = mutableListOf<Directory>()
        if (search(this)) {
            result += this
        }
        for (child in children) {
            result += child.find(search)
        }
        return result
    }

    fun size(): Int {
        var size = 0
        for (child in children) {
            size += child.size()
        }
        for (file in files) {
            size += file.size
        }
        return size
    }
}

fun main() {
    val root = Directory()
    var workingDirectory = root
    loadResource("day-7-input")?.path?.let {
        File(it).forEachLine {line ->
        val inputs = splitOnWhitespace(line)
        when(inputs[0]) {
            "$" -> {
                when (inputs[1]) {
                    "cd" -> {
                        workingDirectory = when (inputs[2]) {
                            "/" -> {
                                root
                            }
                            ".." -> {
                                workingDirectory.parent!!
                            }
                            else -> {
                                val directory = Directory()
                                workingDirectory.addChild(directory)
                                directory
                            }
                        }
                    }
                    "ls" -> {
                        // Do nothing
                    }
                }
            }
            "dir" -> {
                // Do nothing
            }
            else -> {
                workingDirectory.addFile(NamedFile(inputs[1], inputs[0].toInt()))
            }
        }
    }
    }
    val result = root.find { dir -> dir.size() < 100000 }.fold (0) { acc, dir -> acc + dir.size() }
    println("Part 1 result: $result")

    val result2 = root.find { dir -> dir.size() > 8381165 }.fold(70000000) {acc, dir -> if (dir.size() < acc) dir.size() else acc}
    println("Part 2 result: $result2")
}