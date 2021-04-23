package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.zip.ZipEntry

/**
 * Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 *
 * 增量更新：
 * （1）、clean之后，第一次编译，此时对Transform来说不是增量编译， transform方法中isIncremental = false；
 * （2）、不做任何改变直接进行第二次编译，Transform别标记为up-to-date，被跳过执行transform；
 * （3）、修改一个文件中代码，进行第三次编译，此时对Transform来说不是增量编译，transform方法中isIncremental = false。
 *
 */
class RouterMappingTransform extends Transform {


    private WaitableExecutor waitableExecutor = WaitableExecutor.useDirectExecutor()

    @Override
    String getName() {
        return "router_mapping"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        TaskManager
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        boolean isIncremental = transformInvocation.isIncremental()
        println "isIncremental  " + isIncremental

        if (!isIncremental) transformInvocation.getOutputProvider().deleteAll()

        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                processDirectory(directoryInput, transformInvocation, isIncremental)
            }

            // Jar的输入
            input.jarInputs.each { JarInput jarInput ->
                processJarInput(jarInput, transformInvocation, isIncremental)
            }
        }
    }


    private void processDirectory(DirectoryInput directoryInput, TransformInvocation transformInvocation, boolean isIncremental) {
        def destCurrentTransformOutDir =
                transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.name,// 一般不会相同
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

        eachFileRecurse(directoryInput.file, destCurrentTransformOutDir)

        //将目录从一个路径复制到另一路径。如果目标目录存在，则将合并文件内容，并且源目录中的文件将覆盖目标中的文件。
        FileUtils.copyDirectory(directoryInput.file, destCurrentTransformOutDir)
    }


    private void eachFileRecurse(File file, File destTransformDir) {
        if (!file.isDirectory()) {
            if (filter(file.name)) processClass(new FileInputStream(file), destTransformDir)
            return
        }

        File[] files = file.listFiles()
        if (files == null) return

        for (int i = 0; i < files.length; i++) {
            File tmpFile = files[i]
            if (tmpFile.isDirectory()) {
                eachFileRecurse(tmpFile, destTransformDir)
            } else {
                if (filter(tmpFile.name)) processClass(new FileInputStream(tmpFile), destTransformDir)
            }
        }
    }

    private void processJarInput(
            JarInput jarInput,
            TransformInvocation transformInvocation,
            boolean isIncremental) {

        if (!jarInput.file.name.endsWith(".jar")) return


        def destCurrentTransformOutDir =
                transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )


        JarFile jarFile = new JarFile(jarInput.file)
        Enumeration<JarFile> enumeration = jarFile.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            String entryName = jarEntry.name

            //过滤
            if (!filter(entryName)) continue

            ZipEntry zipEntry = new ZipEntry(entryName)

            if (zipEntry.isDirectory()) continue

            // 多去Jar中的Class文件
            InputStream jarFileInput = jarFile.getInputStream(zipEntry)

            println "JarFile entryName >>>> " + entryName

            if (filter(entryName)) processClass(jarFileInput, destCurrentTransformOutDir)
        }


        //todo 处理Jar中的class织入代码

        // 然后将处理过jar复制到destFile
        //从一个路径到另一个路径选择一个常规文件，并保留文件属性。如果目标文件存在，它将被覆盖。
        FileUtils.copyFile(jarInput.file, destCurrentTransformOutDir)
    }


    // 处理Class文件
    private void processClass(InputStream classFileInput, File destCurrentTransformOutDir) {
        println "processClass>>>> " + destCurrentTransformOutDir.absolutePath
    }

    //过滤
    private boolean filter(String name) {
        return name.endsWith(".class") &&
                !name.contains("R\$") &&
                !name.contains("R.class") &&
                !name.contains("BuildConfig.class") &&
                name.endsWith("\$\$Module\$\$RouterMapping.class")
    }
}