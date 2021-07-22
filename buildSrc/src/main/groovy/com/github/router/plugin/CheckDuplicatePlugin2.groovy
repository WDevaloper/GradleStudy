package com.github.router.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * 这种方式不涉及到打包过程，是直接在检测的工程的资源目录
 *
 * 不能检测到第三方库    所以不建议使用这种方式
 *
 */
class CheckDuplicatePlugin2 implements Plugin<Project> {

    private Logger mLogging

    @Override
    void apply(Project project) {
        mLogging = project.getLogger()
        Map<String, Set<File>> sourceSets = new HashMap<>()


        project.rootProject.subprojects { Project target ->
            target.afterEvaluate { Project innerProject ->
                def hasAppPlugin = innerProject.plugins.hasPlugin(AppPlugin.class)
                def hasLibPlugin = innerProject.plugins.hasPlugin(LibraryPlugin.class)


                String projectName = innerProject.getName()
                Set<File> source = sourceSets.get(projectName)
                if (source == null) {
                    source = new HashSet<>()
                }

                if (hasAppPlugin) {
                    innerProject.getExtensions()
                            .getByType(AppExtension.class)
                            .getSourceSets().each {
                        if (!it.res.name.contains("test")) {
                            source.addAll(it.res.srcDirs)
                            source.addAll(it.assets.srcDirs)
                            sourceSets.put(projectName, source)
                        }
                    }
                }

                if (hasLibPlugin) {
                    innerProject.getExtensions()
                            .getByType(LibraryExtension.class)
                            .getSourceSets()
                            .each {
                                if (!it.res.name.contains("test")) {
                                    source.addAll(it.res.srcDirs)
                                    source.addAll(it.assets.srcDirs)
                                    sourceSets.put(projectName, source)
                                }
                            }
                }
            }
        }

        project.gradle.buildFinished {
            sourceSets.keySet().each {
                mLogging.error("sourceSets>>>> Module Name: ${it}")
                sourceSets.get(it).each {
                    mLogging.error("sourceSets: ${it.absolutePath}")
                }
            }
        }
    }

}