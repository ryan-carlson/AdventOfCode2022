package utilities

import java.net.URL

fun loadResource(path: String): URL {
    return Thread.currentThread().contextClassLoader.getResource(path)
        ?: throw Error("Input file not found at path $path")
}