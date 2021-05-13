package com.github.router.runtime

import android.util.Log

object Router {


    private const val TAG: String = "RouterTag"

    private const val PARAMETER_SUFFIX = "\$\$Parameter"

    private const val MAPPING_GENERATE: String =
        "com.github.router.mapping.generated.RouterMapping"

    private val mapping: HashMap<String, Class<*>> = HashMap()


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
    fun inject(target: Any) {
        try {
            val className = "${target.javaClass.name}$PARAMETER_SUFFIX"
            (Class.forName(className).newInstance() as ParameterInject).inject(target)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "Error while inject : ${ex.message}")
        }
    }
}