package com.github.router.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter


class ModifyClassByteCodeBuilder {
    /**
     *
     * @param srcDir 源文件目录
     * @param srcFile 源文件
     * @param destFile 目标万金
     * @return
     */
    void weaveFile(String srcRootDir, File srcFile, File destDir) {
        if (srcFile.isFile()) {
            String outFileName = srcFile.absolutePath.replace(srcRootDir, destDir.absolutePath)
            File outputFile = new File(outFileName)

            if (!outputFile.parentFile.exists()) {
                outputFile.parentFile.mkdirs()
            }
            FileOutputStream fos = new FileOutputStream(outputFile)


            FileInputStream inputStream = new FileInputStream(srcFile)

            if (!srcFile.name.contains("IUpdateImpl")) {//不做处理
                fos.write(inputStream.bytes)
                inputStream.close()
                fos.close()
                return
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
            ModifyClassVisitor classVisitor = new ModifyClassVisitor(classWriter)

            ClassReader classReader = new ClassReader(inputStream)
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)

            fos.write(classWriter.toByteArray())
            fos.close()
            inputStream.close()

            return
        }

        // 目录
        File[] childFiles = srcFile.listFiles()

        if (childFiles == null || childFiles.length <= 0) return

        childFiles.each { File file -> weaveFile(srcRootDir, file, destDir) }
    }
}