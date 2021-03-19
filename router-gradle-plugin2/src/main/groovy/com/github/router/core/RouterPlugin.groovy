package com.github.router.core

import com.github.router.extension.RouterExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

// 路由插件
class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("${target.name} module apply ===>Router Plugin")
        // 创建一个新扩展并将其添加到此容器。
        target.getExtensions().create("router", RouterExtension)
        target.afterEvaluate {
            RouterExtension routerInfo = target["router"]
            println "routerInfo = ${routerInfo.wikiDir}"
        }
    }
}