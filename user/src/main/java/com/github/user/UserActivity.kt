package com.github.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.router.annotate.Destination


@Destination(url = "/user/UserActivity", description = "用户信息")
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}