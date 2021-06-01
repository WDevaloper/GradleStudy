package com.github.router.extension

import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class AndroidExtension {
    Integer compileSdkVersion
    String buildToolsVersion
    DefaultConfig defaultConfig = new DefaultConfig()

    void defaultConfig(Action<DefaultConfig> action) {
        action.execute(defaultConfig)
    }

    void defaultConfig(Closure<DefaultConfig> defaultConfigClosure) {
        ConfigureUtil.configure(defaultConfigClosure, defaultConfig)
    }
}