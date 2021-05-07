package com.github.router.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

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

        transformInvocation.inputs.each { TransformInput input ->
            // 目录的输入
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 得到当前Transform输出目录
                def destDir = transformOutputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

                collect(directoryInput.file.absolutePath, directoryInput.file, destDir)
                //FileUtils.copyDirectory(directoryInput.file, destDir)
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

                FileUtils.copyFile(jarInput.file, destFile)
            }
        }

    }


    void collect(String srcDir, File classFile, File destFile) {

        if (classFile.isFile()) {

            File outputFile = new File(
                    classFile.absolutePath.replace(srcDir, destFile.absolutePath))

            if (!outputFile.parentFile.exists()) {
                outputFile.parentFile.mkdirs()
            }


            FileOutputStream fos = new FileOutputStream(outputFile)

            FileInputStream inputStream = new FileInputStream(classFile)

            if (classFile.name.contains("IUpdateImpl")) {
                ClassReader classReader = new ClassReader(inputStream)

                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
                ModifyClassVisitor classVisitor = new ModifyClassVisitor(classWriter)

                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)

                byte[] data = classWriter.toByteArray()
                fos.write(data)
            } else {//不做处理
                byte[] srcData = new byte[1024]
                while (inputStream.read(srcData) != -1) {
                    fos.write(srcData)
                }
            }
            return
        }

        File[] childFiles = classFile.listFiles()

        if (childFiles == null || childFiles.length <= 0) return

        childFiles.each { File file -> collect(srcDir, file, destFile) }
    }


    class ModifyClassVisitor extends ClassVisitor {
        ModifyClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor)
        }

        @Override
        void visit(
                int version,
                int access,
                String name,
                String signature,
                String superName,
                String[] interfaces) {
            //name>>>>com/github/gradle/mapping/update/IUpdateImpl , superName>>>>>com/github/gradle/mapping/update/IUpdate
            // 修改为com.github.gradle.mapping.update.IUpdate2
            String pendingClassName = "com/github/gradle/mapping/update/IUpdate2"
            println "将$name 的父类：$superName  修改为：$pendingClassName"
            println "signature>>>>>>>$signature"
            super.visit(version, access, name, signature, pendingClassName, interfaces)
        }


        @Override
        MethodVisitor visitMethod(
                int access,
                String name,
                String descriptor,
                String signature,
                String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(
                    access,
                    name,
                    descriptor,
                    signature,
                    exceptions)
            // 修改构造方法体内容
            methodVisitor.visitCode()
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "com/github/gradle/mapping/update/IUpdate2",
                    "<init>",
                    "()V",
                    false)
            methodVisitor.visitInsn(Opcodes.RETURN)
            methodVisitor.visitMaxs(1, 1)
            methodVisitor.visitEnd()
            return methodVisitor
        }
    }
}