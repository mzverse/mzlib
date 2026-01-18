package mz.mzlib.demokt

import org.junit.jupiter.api.Test

class Test {
    @Test
    fun test1() {
        for (i in iterator {
            for (i in 0 until 10)
                yield(i)
            yield(114)
            for (i in 10 until 20)
                yield(i)
        }) {
            println(i)
        }
    }
}