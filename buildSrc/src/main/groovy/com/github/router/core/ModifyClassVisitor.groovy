package com.github.router.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ModifyClassVisitor extends ClassVisitor   {
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