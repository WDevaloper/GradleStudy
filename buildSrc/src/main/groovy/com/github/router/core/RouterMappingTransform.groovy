package com.github.router.core

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils


class RouterMappingTransform extends Transform {

    @Override
    String getName() {
        return "router_mapping"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "transform>>>>>>>"

        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                def destDir = transformInvocation
                        .getOutputProvider()
                        .getContentLocation(
                                directoryInput.name,// 一般不会相同
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY)
                //将目录从一个路径复制到另一路径。如果目标目录存在，则将合并文件内容，并且源目录中的文件将覆盖目标中的文件。
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }
            // Jar的输入
            input.jarInputs.each { JarInput jarInput ->
                def destFile = transformInvocation
                        .getOutputProvider()
                        .getContentLocation(
                                jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR
                        )
                FileUtils.copyFile(jarInput.file, destFile)
            }
        }
    }
}