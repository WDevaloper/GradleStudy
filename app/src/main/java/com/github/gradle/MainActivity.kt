package com.github.gradle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.github.modify.IUpdateImpl
import com.github.router.annotate.Destination
import com.github.router.annotate.Parameter
import com.github.router.runtime.Router
import com.github.user.UserInfo


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

        Router
            .build("/app/AptActivity")
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

        val userInfo = UserInfo()
        userInfo.getUser()


    }


    object CrashUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        @Volatile
        private var mDefaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

        @JvmStatic
        fun init(context: Context) {

            val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (defaultUncaughtExceptionHandler != this) {
                mDefaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler
                Thread.setDefaultUncaughtExceptionHandler(this)
            }
        }

        override fun uncaughtException(t: Thread, e: Throwable) {
            Log.e(
                "tag",
                "${Thread.currentThread().name}   uncaughtException:  thread name: ${t.name} ${e.message}"
            )
            if (mDefaultUncaughtExceptionHandler != this) {
                //mDefaultUncaughtExceptionHandler?.uncaughtException(t, e)
            }
        }
    }

    fun click(view: View) {
        1.div(0)
    }
}