package com.github.router.runtime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import java.io.Serializable
import java.lang.RuntimeException
import java.util.ArrayList

object Router {


    private const val TAG: String = "RouterTag"


    private const val MAPPING_GENERATE: String =
        "com.github.router.mapping.generated.RouterMapping"

    private val mapping: HashMap<String, Class<*>> = HashMap()

    // 目标Class
    private var targetClass: Class<*>? = null

    // 管理bundle数据
    private lateinit var bundleManager: BundleManager

    @JvmStatic
    fun init() {
        try {
            val mappingClass: Class<*> = Class.forName(MAPPING_GENERATE)
            val mappingGetMethod = mappingClass.getMethod("get")
            val mappings = mappingGetMethod.invoke(null) as? Map<String, Class<*>>
            if (mappings != null && mappings.isNotEmpty()) {
                mappings.forEach { Log.i(TAG, "init: ${it.key} --> ${it.value.simpleName}") }
                mapping.putAll(mappings)
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "init: Error while router: $ex")
        }
    }


    @JvmStatic
    fun inject(target: Any) = ParameterManager.inject(target)

    fun build(path: String): Router {
        bundleManager = BundleManager()
        targetClass = mapping[path]
        if (targetClass == null) throw RuntimeException("Class not found for path: $path")
        return this
    }


    fun withString(key: String, value: String): Router {
        bundleManager.withString(key, value)
        return this
    }

    fun withBoolean(key: String, value: Boolean): Router {
        bundleManager.withBoolean(key, value)
        return this
    }

    fun withInt(key: String, value: Int): Router {
        bundleManager.withInt(key, value)
        return this
    }

    fun withLong(key: String, value: Long): Router {
        bundleManager.withLong(key, value)
        return this
    }

    fun withBundle(bundle: Bundle): Router {
        bundleManager.withBundle(bundle)
        return this
    }

    fun withIntArray(key: String, value: IntArray): Router {
        bundleManager.withIntArray(key, value)
        return this
    }

    fun withStringArray(key: String, value: Array<String>): Router {
        bundleManager.withStringArray(key, value)
        return this
    }

    fun withParcelable(key: String, value: Parcelable): Router {
        bundleManager.withParcelable(key, value)
        return this
    }

    fun withSerializable(key: String, value: Serializable): Router {

        bundleManager.withSerializable(key, value)
        return this
    }

    fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>): Router {
        bundleManager.withParcelableArrayList(key, value)
        return this
    }

    // 这里可以拓展。根据类型是Activity 还是其他类型

    fun navigation(context: Context): Any {
        return Intent(context, targetClass)
            .putExtras(bundleManager.bundle)
            .run { context.startActivity(this) }
    }
}