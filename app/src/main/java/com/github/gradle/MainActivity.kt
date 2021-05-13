package com.github.gradle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.modify.IUpdateImpl
import com.github.router.annotate.Destination
import com.github.router.annotate.Parameter
import com.github.router.runtime.Router


@Destination(url = "/app/MainActivity", description = "首页")
class MainActivity : AppCompatActivity() {

    @Parameter(name = "param")
    @JvmField
    var param: String? = null

    @Parameter(name = "param2")
    @JvmField
    var param2 = 0


    @Parameter(name = "param3")
    @JvmField
    var param3: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iUpdateImpl = IUpdateImpl()
        iUpdateImpl.update()

        Router.build("/app/AptActivity")
            .withString("param", "hello apt")
            .withSerializable("person", Person())
            .withInt("param2", 1)
            .withLong("long_param", 2L)
            .withLong("long_param2", 2L)
            .withIntArray("int_param", intArrayOf(1, 2, 3))
            .withStringArray("string_param", arrayOf("qwe"))
            .withParcelableArrayList("list_param", arrayListOf(User()))
            .withParcelable("user", User())
            .navigation(this)
    }
}