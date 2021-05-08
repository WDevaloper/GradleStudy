package com.github.router.core

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class ModifyClassByteCodeBuilder {
    /**
     *
     * @param srcDir 源文件目录
     * @param srcFile 源文件
     * @param destFile 目标万金
     * @return
     */
    void weaveByteCodeFromDir(String srcRootDir, File srcFile, File destDir) {
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
            } else {
                realWeaveCode(inputStream, fos)
            }

            fos.close()
            inputStream.close()
            return
        }

        // 目录
        File[] childFiles = srcFile.listFiles()

        if (childFiles == null || childFiles.length <= 0) return

        childFiles.each { File file -> weaveByteCodeFromDir(srcRootDir, file, destDir) }
    }


    void weaveByteCodeFromJatFile(File jarFile, File destFile) {

        // 输出目标jar文件
        FileOutputStream fos = new FileOutputStream(destFile)
        JarOutputStream destJarOs = new JarOutputStream(fos)


        JarFile srcJarFile = new JarFile(jarFile)
        Enumeration<JarEntry> entries = srcJarFile.entries()

        while (entries.hasMoreElements()) {
            JarEntry srcJarEntry = entries.nextElement()

            //com/github/gradle/mapping/hotfit$$Module$$RouterMapping.class
            String srcJarEntryName = srcJarEntry.getName()

            // 拿到jar包里面的输入流
            InputStream srcJarEntryIs = srcJarFile.getInputStream(srcJarEntry)

            // 构造目标jar文件里的文件实体
            destJarOs.putNextEntry(new ZipEntry(srcJarEntryName))


            if (!srcJarEntryName.contains("IUpdateImpl")) {//不做处理
                destJarOs.write(IOUtils.toByteArray(srcJarEntryIs))
            } else {
                realWeaveCode(srcJarEntryIs, destJarOs)
            }

            srcJarEntryIs.close()
        }

        destJarOs.closeEntry()
        destJarOs.close()
        srcJarFile.close()
    }


    private static void realWeaveCode(InputStream inputStream, OutputStream outputStream) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ModifyClassVisitor classVisitor = new ModifyClassVisitor(classWriter)

        ClassReader classReader = new ClassReader(inputStream)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)

        outputStream.write(classWriter.toByteArray())
    }
}