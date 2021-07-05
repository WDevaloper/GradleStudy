package com.github.gradle

import com.github.gradle.mapping.StartTest
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    class Book() {
        var name = "《数据结构》"
        var price = 60
        fun displayInfo() = print("Book name : $name and price : $price")
    }


    @Test
    fun addition_isCorrect() {
        val book = Book().let {
            it.name = "《计算机网络》"
            "This book is ${it.name}"
        }
        print(book)

        val book1 = Book()

        //用于初始化对象或更改对象属性，可使用apply
        //如果将数据指派给接收对象的属性之前验证对象，可使用also
        //如果将对象进行空检查并访问或修改其属性，可使用let
        //如果是非null的对象并且当函数块中不需要返回值时，可使用with
        //如果想要计算某个值，或者限制多个本地变量的范围，则使用run

        //返回值为let块的最后一行或指定return表达式。
        val let = book1.let {
            1 + 6
        }

        val also = book1.also {
            ""
        }
        also.name

        //主要用于初始化或更改对象，因为它用于在不使用对象的函数的情况下返回自身
        val apply = book1.apply {
        }
        apply.name


        //当 lambda 表达式同时包含对象初始化和返回值的计算时，run更适合
        val run = book1.run {
            ""
        }
        run.length

        //with使用的是非null的对象，当函数块中不需要返回值时，可以使用with。
        with(book1) {
        }
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