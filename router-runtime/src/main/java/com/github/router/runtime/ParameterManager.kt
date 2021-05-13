package com.github.router.runtime

import androidx.collection.LruCache

object ParameterManager {

    private val parameterInjectCache: LruCache<String, ParameterInject> by lazy { LruCache(128) }

    // APT生成的获取参数源文件，后缀名
    private const val PARAMETER_SUFFIX = "\$\$Parameter"

    @JvmStatic
    fun inject(target: Any) {
        val targetClassName = target.javaClass.name
        var parameterInject: ParameterInject? = parameterInjectCache.get(targetClassName)
        try {
            if (parameterInject == null) {
                parameterInject =
                    Class.forName("$targetClassName$PARAMETER_SUFFIX")
                        .newInstance() as? ParameterInject
                parameterInjectCache.put(targetClassName, parameterInject!!)
            }
            parameterInject.inject(target)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }
}