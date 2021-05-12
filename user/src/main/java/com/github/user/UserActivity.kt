package com.github.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.router.annotate.Destination
import com.github.router.annotate.Parameter


@Destination(url = "/user/UserActivity", description = "用户信息")
class UserActivity : AppCompatActivity() {

    @Parameter(name = "userName")
    @JvmField
    var userName: String? = null

    @Parameter(name = "age")
    @JvmField
    var age: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}