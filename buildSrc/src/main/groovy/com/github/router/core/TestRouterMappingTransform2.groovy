package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

/**
 * Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 */
class TestRouterMappingTransform2 extends Transform {

    @Override
    String getName() {
        return "TestRouterMappingTransform2"
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


        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                def destDir = transformInvocation
                        .getOutputProvider()
                        .getContentLocation(
                                directoryInput.name + "test",// 一般不会相同
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY)

                //E:\developer\project\GradleStudy\app\build\intermediates\javac\debug\classes
                // E:\developer\project\GradleStudy\app\build\tmp\kapt3\classes\debug
                println "test directoryInput>>> " + directoryInput.file.absolutePath

                //这是Transform的输出目录：E:\developer\project\GradleStudy\app\build\intermediates\transforms\TestRouterMappingTransform2\debug\40
                println "test destDir>>> " + destDir.absolutePath


                if (isIncremental) {
                    //build\intermediates\javac\debug\classes
                    String srcDirPath = directoryInput.file.absolutePath
                    //build\intermediates\transforms\TestRouterMappingTransform2\debug\40
                    String destDirPath = destDir.absolutePath

                    Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()
                    fileStatusMap.each { Map.Entry<File, Status> entry ->
                        File inputFile = entry.getKey()
                        Status status = entry.getValue()

                        // build\intermediates\javac\debug\classes\com\github\gradle\AptActivity.class
                        println "test inputFile>>> " + inputFile.absolutePath
                        println "test status>>> "+status.name()

                        String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath)
                        File destFile = new File(destFilePath)
                        switch (status) {
                            case Status.NOTCHANGED:
                                break
                            case Status.REMOVED:
                                if (destFile.exists()) {
                                    //FileUtils.forceDelete(destFile)
                                }
                                break
                            case Status.ADDED:
                            case Status.CHANGED:
                                //FileUtils.touch(destFile)
                                //transformSingleFile(inputFile, destFile, srcDirPath)
                                break
                        }
                    }

                }

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
                //从一个路径到另一个路径选择一个常规文件，并保留文件属性。如果目标文件存在，它将被覆盖。
                FileUtils.copyFile(jarInput.file, destFile)
            }
        }
    }
}