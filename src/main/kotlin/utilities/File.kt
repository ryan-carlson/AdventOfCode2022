package utilities

import java.io.File
import java.net.URL

fun loadResource(path: String): URL {
    return Thread.currentThread().contextClassLoader.getResource(path)
        ?: throw Error("Input file not found at path $path")
}

fun readLines(path: String): List<String> {
    return loadResource("day-16-input").path.let {
        File(it).readLines()
    }
}