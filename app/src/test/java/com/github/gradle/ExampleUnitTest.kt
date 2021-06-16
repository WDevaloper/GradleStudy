package com.github.gradle

import com.github.gradle.mapping.StartTest
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        StartTest.main(null)
    }

    @Test
    fun mainThread() {

        Thread {
            println("thread name:" + Thread.currentThread().name + " start run");

            1.div(0)

            println("thread name:" + Thread.currentThread().name + " end run");
        }.start()


        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        println("thread name:" + Thread.currentThread().name + " end...")
    }
}