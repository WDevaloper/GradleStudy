package com.github.gradle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.router.annotate.Destination


@Destination(url = "/app/MainActivity", description = "路由测试")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}