package com.github.router.core

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.tasks.MergeResources
import org.antlr.v4.misc.Utils
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskInputs

import java.util.function.Consumer


class CWebpPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def android = target.getExtensions().getByType(AppExtension)

        target.afterEvaluate {
            def applicationVariants = android.getApplicationVariants()
            applicationVariants.all(new Action<ApplicationVariant>() {

                @Override
                void execute(ApplicationVariant applicationVariant) {
                    //获得: debug/release
                    String variantName = applicationVariant.getName()
                    // 首个字母大写
                    String capitalizeName = Utils.capitalize(variantName)


                    println "resourceFiles >>>"
                    getResource(target, capitalizeName)
                    getResource2(target, applicationVariant)
                    getResource3(target, applicationVariant, capitalizeName)
                }
            })
        }
    }


    void eachFileRecurse(File file) {
        if (file.isFile()) {
            println "resourceFiles >>> ${childFile.name}"
            return
        }

        File[] childFiles = file.listFiles()

        if (childFiles == null) return

        for (int i = 0; i < childFiles.size(); i++) {
            File childFile = childFiles.getAt(i)
            if (childFile.isDirectory()) {
                eachFileRecurse(childFile)
            } else {
                println "resourceFiles >>> ${childFile.name}"
            }
        }
    }


    private void getResource(Project target, String capitalizeName) {

        TaskContainer taskContainer = target.getTasks()
        String taskName = "merge" + capitalizeName + "Resources"
        Task mergeResTask = taskContainer.findByName(taskName)


        mergeResTask.doFirst(new Action<Task>() {
            @Override
            void execute(Task task) {
                Set<File> resourceFiles = task.getInputs().files.getFiles()
                new Thread() {
                    @Override
                    void run() {
                        resourceFiles.forEach(new Consumer<File>() {
                            @Override
                            void accept(File file) {
                                eachFileRecurse(file)
                            }
                        })
                    }
                }.start()
            }
        })
    }

    private void getResource2(Project project, ApplicationVariant variant) {
        //获得 mergeResources 的task
        MergeResources mergeResources = variant.getMergeResourcesProvider().get()
        mergeResources.doFirst(new Action<Task>() {
            @Override

            public void execute(Task task) {
                TaskInputs outputs = task.getInputs()
                Set<File> files = outputs.getFiles().getFiles()
                new Thread(){
                    @Override
                    void run() {
                        files.forEach(new Consumer<File>() {
                            @Override
                            void accept(File file) {
                                eachFileRecurse(file)
                            }
                        })
                    }
                }.start()
            }
        })
    }


    private void getResource3(Project project,
                              ApplicationVariant variant,
                              String capitalizeName) {
        //获得 mergeResources 的task
        MergeResources mergeResources =
                variant.getMergeResourcesProvider().get()
        // 创建自己任务
        Task convertTask = project.task("convertTask" + capitalizeName)
        convertTask.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                Set<File> files = variant.getAllRawAndroidResources().getFiles()
            }
        })
        mergeResources.dependsOn(project.getTasks().findByName(convertTask.getName()))
    }


}