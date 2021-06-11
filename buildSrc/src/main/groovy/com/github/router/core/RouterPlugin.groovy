package com.github.router.core

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.github.router.Constants
import com.github.router.extension.AndroidExtension
import com.github.router.extension.RouterExtension
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.lang.reflect.Field
import java.util.function.Consumer

// 路由插件
class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {


        def hasAppPlugin = target.plugins.hasPlugin(AppPlugin.class)
        def hasLibPlugin = target.plugins.hasPlugin(LibraryPlugin.class)

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new GradleException("RouterPlugin: The 'com.android.application' or 'com.android.library' plugin is required.")
        }

        // 注册Transform 只有App工程才有AppExtension
        if (hasAppPlugin) {
            def android = target.extensions.getByType(AppExtension)
            android.registerTransform(new MKRouterMappingTransform())
            android.registerTransform(new ModifyClassExtendsTransform())
        }

        //内嵌 Extension，其实际本质是方法带了Closure参数
        target.getExtensions().add("androidExtension", AndroidExtension)
        target.afterEvaluate {
            AndroidExtension androidExtension =
                    target.extensions.getByName("androidExtension")
            println androidExtension.defaultConfig.applicationId +
                    ">>>nestedExtensionTest androidExtension>>>>>" +
                    androidExtension.buildToolsVersion
        }

        // 1、自动帮助用户传递路径参数到注解处理器中
        autoInjectParamToAnnotationProcessor(target)
        // 2、实现旧的构建产物自动清理(当前的Project)
        cleanOldBuildProduct(target)

        // 创建一个新扩展并将其添加到此容器。
        target.extensions.create("router", RouterExtension)
        target.afterEvaluate {
            RouterExtension routerInfo = target["router"]
            println "用户设置的WIKI路径： ${routerInfo.wikiDir}"


            // 3、在JavaC(compileDebugJavaWithJavac)任务后，汇总生成文档
            generateDoc(target, routerInfo)
        }

        println "module name >>>>>" + target.name
    }


    private void autoInjectParamToAnnotationProcessor(Project target) {
        /*
        kapt {
          arguments {
               arg("MODULE_NAME", project.getName())
               arg("root_project_dir", rootProject.projectDir.absolutePath)
            }
        }
         */

        def kaptExt = target.extensions.findByName("kapt")
        if (kaptExt != null) {
            kaptExt.arguments {
                arg(Constants.MODULE_NAME, target.getName())
                arg(Constants.ROOT_PROJECT_DIR, target.rootProject.projectDir.absolutePath)
            }
        }
    }

    private static void cleanOldBuildProduct(Project target) {
        target.clean.doFirst {
            File routerMappingDir =
                    new File(target.rootProject.projectDir, Constants.ROUTER_MAPPING_DOC_DIR)
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }
    }


    private static void generateDoc(Project target, RouterExtension routerInfo) {
        // 找到Javac任务
        def javacTasks = target.tasks.findAll { Task task -> task.name.startsWith("compile") && task.name.endsWith("JavaWithJavac") }

        // 理论上只有一个Task，无需遍历
        javacTasks.each { Task task ->
            task.doLast {
                // 每个模块都有javac任务,所以只有宿主或app模块才能生成文档,方便其他Library也生成文档
                def hasAppPlugin = target.plugins.hasPlugin(AppPlugin.class)
                if (hasAppPlugin) generateDocumentsTheJavacAfter(target, routerInfo)
            }
        }
    }

    private static void generateDocumentsTheJavacAfter(Project project, RouterExtension routerInfo) {
        File routerMappingDir =
                new File(project.rootProject.projectDir, Constants.ROUTER_MAPPING_DOC_DIR)
        if (!routerMappingDir.exists()) {
            return
        }


        // 获取到JSON文件
        File[] allChildFiles = routerMappingDir.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File fileDir, String fileName) {
                return fileName.endsWith(".json")
            }
        })

        if (allChildFiles.length <= 0) return

        StringBuilder markdownBuilder = new StringBuilder()
        markdownBuilder.append("# 路由页面文档\n\n")

        allChildFiles.each { child ->
            JsonSlurper jsonSlurper = new JsonSlurper()
            def contentArray = jsonSlurper.parse(child)
            contentArray.each { innerContent ->
                def url = innerContent[Constants.KEY_PATH]
                def description = innerContent[Constants.KEY_DESCRIPTION]
                def realPath = innerContent[Constants.KEY_REAL_PATH]

                markdownBuilder
                        .append("## $description\n")
                        .append("- path: $url\n")
                        .append("- realPath: $realPath\n\n")
            }
        }

        // 数据收集完成把，缓存每个模块的文档删除
        routerMappingDir.deleteDir()

        if (routerInfo.wikiDir == null) {
            routerInfo.wikiDir = project.buildDir.absolutePath
        }

        if (routerInfo.wikiName == null || routerInfo.wikiName.length() < 1) {
            routerInfo.wikiName = Constants.DEFAULT_ROUTER_WIKI_NAME
        }

        File wikiFileDir = new File(routerInfo.wikiDir)

        if (!wikiFileDir.exists()) {
            wikiFileDir.mkdir()
        }


        // 写入文档
        File wikiFile = new File(wikiFileDir, routerInfo.wikiName)
        wikiFile.write(markdownBuilder.toString())
    }
}