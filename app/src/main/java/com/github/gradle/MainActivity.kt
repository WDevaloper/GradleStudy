package com.github.gradle

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iUpdateImpl = IUpdateImpl()
        iUpdateImpl.update()
    }
}