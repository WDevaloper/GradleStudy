package com.github.router.core

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

/**
 * Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 */
class RouterMappingTransform extends Transform {

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

        if (!isIncremental) transformInvocation.getOutputProvider().deleteAll()

        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                processDirectoryInputWithIncremental(directoryInput, transformInvocation, isIncremental)
            }
            // Jar的输入
            input.jarInputs.each { JarInput jarInput ->
                processJarInputWithIncremental(jarInput, transformInvocation, isIncremental)
            }
        }
    }


    private static void processDirectoryInputWithIncremental(DirectoryInput directoryInput, TransformInvocation transformInvocation, boolean isIncremental) {
        def destDir = transformInvocation.getOutputProvider().getContentLocation(
                directoryInput.name,// 一般不会相同
                directoryInput.contentTypes,
                directoryInput.scopes,
                Format.DIRECTORY)

        if (isIncremental) {
            FileUtils.mkdirs(destDir)
            // 输出目录路径
            String destDirPath = destDir.getAbsolutePath()

            // 获取到输入目录所有改变的文件
            Map<File, Status> inputFileStatusMap = directoryInput.getChangedFiles()

            // 遍历获取目录下的所有文件
            inputFileStatusMap.each { Map.Entry<File, Status> entry ->
                File inputFile = entry.getKey()
                Status status = entry.getValue()

                // 输出文件名
                String destFileName = inputFile.name
                // 输出文件   实际上是输入文件目录和文件名 与输入文件目录和文件名 保持一致
                File destFile = new File(destDirPath, destFileName)


                switch (status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.REMOVED:
                        FileUtils.deleteIfExists(destFile)
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        //todo 对 inputFile 处理织入代码
                        println "transform >>>>>dir"
                        // 把输入文件修改处理完成 然后复制相同目录文件名下，直接覆盖原来
                        // 所谓的destFile就是当前Transform目录下的文件，而inputFile则是上一个Transform目录下的文件
                        FileUtils.copyFile(inputFile, destFile)
                        break
                }
            }
        } else {

        }

        //将目录从一个路径复制到另一路径。如果目标目录存在，则将合并文件内容，并且源目录中的文件将覆盖目标中的文件。
        FileUtils.copyDirectory(directoryInput.file, destDir)
    }

    private static void processJarInputWithIncremental(JarInput jarInput, TransformInvocation transformInvocation, boolean isIncremental) {
        def destFile =
                transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )

        if (isIncremental) {
            switch (jarInput.status) {
                case Status.NOTCHANGED:
                    break
                case Status.ADDED:
                case Status.REMOVED://移除Removed
                    FileUtils.deleteIfExists(destFile)
                    break
                case Status.CHANGED: //处理有变化的
                    //Changed的状态需要先删除之前的
                    println "transform >>>>>jar"
                    break
            }
        } else {

        }

        //todo 处理Jar中的class织入代码

        // 然后将处理过jar复制到destFile
        //从一个路径到另一个路径选择一个常规文件，并保留文件属性。如果目标文件存在，它将被覆盖。
        FileUtils.copyFile(jarInput.file, destFile)
    }
}