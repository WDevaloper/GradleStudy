package com.github.router.plugin


import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.github.router.transform.TestIncrementalTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger

import java.util.function.Consumer


class CheckDuplicatePlugin implements Plugin<Project> {

    private Logger mLogging

    @Override
    void apply(Project project) {
        mLogging = project.getLogger()

        AppExtension extension = project.extensions.getByType(AppExtension)
        def variants = extension.getApplicationVariants()

        extension.registerTransform(new TestIncrementalTransform(project))


        project.afterEvaluate {
            variants.all { BaseVariantImpl variant ->
                def name = variant.getName().capitalize()
                def mergeResourceTask = variant.getMergeResourcesProvider().get()

                project.task("checkDuplicate$name") { Task task ->
                    task.setGroup("checkRes")
                    mergeResourceTask.dependsOn(task)

                    task.doLast {
                        Set<File> resFiles = variant.getAllRawAndroidResources().getFiles()
                        resFiles.forEach(new Consumer<File>() {
                            @Override
                            void accept(File file) {
                                eachFileRecurse(file)
                            }
                        })
                    }
                }
            }
        }
    }

    void eachFileRecurse(File file) {
        if (file.isDirectory()) {
            def childFiles = file.listFiles()
            if (childFiles == null && childFiles.length <= 0) {
                return
            }
            childFiles.each { eachFileRecurse(it) }
        } else {
            mLogging.error("CheckDuplicatePlugin: ${file.absolutePath}")
        }
    }
}