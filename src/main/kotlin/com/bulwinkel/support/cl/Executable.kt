package com.bulwinkel.support.cl

fun execForString(cmd: String): String {
    val p = Runtime.getRuntime().exec(cmd)

    val result = p.waitFor()
    if (result != 0) {
        return ""
    }

    return try {
        p.inputStream.bufferedReader().use { it.readText() }
    } catch (e: Throwable) {
        ""
    }
}
