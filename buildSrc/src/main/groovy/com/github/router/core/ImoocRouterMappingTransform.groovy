package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

/**
 * Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 */
class ImoocRouterMappingTransform extends Transform {


    /**
     *
     * @return 不能null
     */
    @Override
    String getName() {
        return "router_mapping"
    }

    /**
     * 返回告知编译器，当前Transform 需要消费的输入类型。
     *
     * 通俗讲就是当前Transform告诉编译器，你帮我收集 CONTENT_CLASS 这种类型的资源
     *
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }


    /**
     * 返回告知编译器，当前Transform需要收集的范围
     *
     * 通俗讲就是当前Transform告诉编译器，你帮我收集 SCOPE_FULL_PROJECT 的资源
     *
     * @return
     */
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

        RouterMappingCollector routerMappingCollector = new RouterMappingCollector()
        // 1、遍历所有Input
        // 2、对Input二次处理
        // 3、对Input拷贝到目标目录
        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 得到当前Transform输出目录
                def destDir = transformOutputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

                routerMappingCollector.collect(directoryInput.file)

                FileUtils.copyDirectory(directoryInput.file, destDir)
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

                routerMappingCollector.collectFromJarFile(jarInput.file)

                FileUtils.copyFile(jarInput.file, destFile)
            }
        }

        RouterMappingByteCodeBuilder.get(routerMappingCollector.getMappingClassNames())
    }
}