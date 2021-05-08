package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

/**
 * 修改指定类的继承关系
 *
 *
 * 需要注意的是修改了父类还需要修改构造方法中super的调用
 *
 */
class ModifyClassExtendsTransform extends Transform {

    @Override
    String getName() {
        return "modify_class_extends"
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
        TransformOutputProvider transformOutputProvider = transformInvocation.getOutputProvider()

        ModifyClassByteCodeBuilder byteCodeBuilder = new ModifyClassByteCodeBuilder()

        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 得到当前Transform输出目录
                def destDir = transformOutputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

                byteCodeBuilder.weaveByteCodeFromDir(
                        directoryInput.file.absolutePath,
                        directoryInput.file,
                        destDir)
            }


            // Jar的输入
            input.jarInputs.each { JarInput jarInput ->
                // 得到当前Transform输出Jar文件
                def destFile = transformOutputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )

                byteCodeBuilder.weaveByteCodeFromJatFile(jarInput.file, destFile)

                FileUtils.copyFile(jarInput.file, destFile)
            }
        }
    }
}