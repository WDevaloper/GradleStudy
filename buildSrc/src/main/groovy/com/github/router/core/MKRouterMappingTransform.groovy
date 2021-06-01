package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.api.variant.VariantInfo
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 *
 *
 * 目前实现Router Transform有两种方式;
 *  1、先定义路由总表类（RouterMapping），通过Transform收集，apt生成各个模的子路由表，然后通过ASM注入路由总表类（RouterMapping）
 *  2、与第一种方式相反，也就是现在这种，通过Transform收集apt生成各个模的子路由表，通过ASM生成路由总表
 *
 */
class MKRouterMappingTransform extends Transform {

    @Override
    boolean applyToVariant(VariantInfo variant) {
        println "applyToVariant buildTypeName >>>>>>>>> " + variant.buildTypeName
        println "applyToVariant fullVariantName >>>>>>>>> " + variant.fullVariantName
        println "applyToVariant flavorNames: >>>>>>>>> " + variant.flavorNames.toListString()
        return super.applyToVariant(variant)
    }


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

                // 收集类名
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

                // 收集类名
                routerMappingCollector.collectFromJarFile(jarInput.file)

                FileUtils.copyFile(jarInput.file, destFile)
            }
        }


        // 将生成的RouterMapping写入Transform生成Jar包中
        File routerMappingJarFile =
                transformInvocation.getOutputProvider()
                        .getContentLocation("router_mapping",
                                getInputTypes(), getScopes(), Format.JAR)

        println getName() + " >>>>>>>>> " + routerMappingJarFile.absolutePath

        FileOutputStream fos = new FileOutputStream(routerMappingJarFile)
        JarOutputStream jarFos = new JarOutputStream(fos)
        // jar包中文件
        ZipEntry zipEntry = new ZipEntry(RouterMappingByteCodeBuilder.CLASS_NAME + ".class")
        // 把文件放入jar中
        jarFos.putNextEntry(zipEntry)
        // 把数据写入文件
        jarFos.write(
                RouterMappingByteCodeBuilder.get(
                        routerMappingCollector.getMappingClassNames()))
        jarFos.closeEntry()
        jarFos.close()
        fos.close()
    }
}