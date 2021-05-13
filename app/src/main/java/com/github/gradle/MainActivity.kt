package com.github.gradle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.modify.IUpdateImpl
import com.github.router.annotate.Destination
import com.github.router.annotate.Parameter


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

        val intent = Intent(this, AptActivity::class.java)
        intent.putExtra("param", "hello apt")
        intent.putExtra("person", Person())
        intent.putExtra("param2", 1)
        intent.putExtra("long_param", 2L)
        intent.putExtra("long_param2", 2L)
        intent.putExtra("int_param", intArrayOf(1, 2, 3))
        intent.putExtra("string_param", arrayOf("qwe"))
        intent.putExtra("list_param", arrayListOf(User()))
        intent.putExtra("user", User())
        intent.putExtra("person", Person())
        startActivity(intent)
    }
}