package com.github.router.core

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.github.router.extension.RouterExtension
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

// 路由插件
class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def hasAppPlugin = target.plugins.hasPlugin(AppPlugin.class)
        def hasLibPlugin = target.plugins.hasPlugin(LibraryPlugin.class)

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new GradleException("RouterPlugin: The 'com.android.application' or 'com.android.library' plugin is required.")
        }

        // 1、自动帮助用户传递路径参数到注解处理器中
        autoInjectParamToAnnotationProcessor(target)
        // 2、实现旧的构建产物自动清理
        cleanOldBuildProduct(target)

        // 创建一个新扩展并将其添加到此容器。
        target.extensions.create("router", RouterExtension)
        target.afterEvaluate {
            RouterExtension routerInfo = target["router"]
            println "用户设置的WIKI路径： ${routerInfo.wikiDir}"


            // 3、在JavaC(compileDebugJavaWithJavac)任务后，汇总生成文档
            generateDoc(target, routerInfo)
        }
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
                arg("MODULE_NAME", target.getName())
                arg("root_project_dir", target.rootProject.projectDir.absolutePath)
            }
        }
    }

    private void cleanOldBuildProduct(Project target) {
        target.clean.doFirst {
            File routerMappingDir =
                    new File(target.rootProject.projectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }
    }


    private Set<Task> generateDoc(Project target, RouterExtension routerInfo) {
        target.tasks.findAll { Task task ->
            task.name.startsWith("compile") && task.name.endsWith("JavaWithJavac")
        }.each { Task task ->
            task.doLast {
                println("task>>>" + task.name)
                def hasAppPlugin = target.plugins.hasPlugin(AppPlugin.class)
                if (hasAppPlugin) {
                    // 每个模块都有javac任务,
                    generateDocumentsTheJavacAfter(target, routerInfo)
                }
            }
        }
    }

    private void generateDocumentsTheJavacAfter(Project project, RouterExtension routerInfo) {
        File routerMappingDir =
                new File(project.rootProject.projectDir, "router_mapping")
        println("routerMappingDir >>>>>" + routerMappingDir)
        if (!routerMappingDir.exists()) {
            return
        }


        // 获取到JSON文件
        File[] allChildFiles = routerMappingDir.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File fileDir, String fileName) {
                println("routerMappingDir >>>>>" + fileName)
                return fileName.endsWith(".json")
            }
        })

        if (allChildFiles.length <= 0) {
            return
        }

        StringBuilder markdownBuilder = new StringBuilder()
        markdownBuilder.append("# 页面文档\n\n")

        allChildFiles.each { child ->
            JsonSlurper jsonSlurper = new JsonSlurper()
            def contentArray = jsonSlurper.parse(child)
            contentArray.each { innerContent ->
                def url = innerContent["url"]
                def description = innerContent["description"]
                def realPath = innerContent["realPath"]

                markdownBuilder
                        .append("## $description\n")
                        .append("- url: $url\n")
                        .append("- realPath: $realPath\n\n")
            }
        }

        if (routerInfo.wikiDir == null) {
            throw new RuntimeException("routerInfo.wikiDir not null")
        }

        File wikiFileDir = new File(routerInfo.wikiDir)

        if (!wikiFileDir.exists()) {
            wikiFileDir.mkdir()
        }

        if (routerInfo.wikiName == null || routerInfo.wikiName.length() < 1) {
            routerInfo.wikiName = "路由页面文档.md"
        }

        File wikiFile = new File(wikiFileDir, routerInfo.wikiName)
        wikiFile.write(markdownBuilder.toString())
    }
}