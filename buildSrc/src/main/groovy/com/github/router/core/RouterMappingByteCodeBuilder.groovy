package com.github.router.core

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class RouterMappingByteCodeBuilder implements Opcodes {
    public static final String CLASS_NAME =
            "com/github/router/mapping/generated/RouterMapping"

    static byte[] get(Set<String> classNames) {


        //

        // 4、返回map


        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        MethodVisitor methodVisitor

        // 1、创建一个类
        classWriter.visit(
                V1_8,
                ACC_PUBLIC | ACC_SUPER,
                CLASS_NAME,
                null,
                "java/lang/Object",
                null)
        // 2、创建构造方法
        methodVisitor = classWriter.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null)

        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false)
        methodVisitor.visitInsn(RETURN)
        methodVisitor.visitMaxs(1, 1)
        methodVisitor.visitEnd()

        // 3、创建get方法
        methodVisitor = classWriter.visitMethod(
                ACC_PUBLIC | ACC_STATIC,
                "get",
                "()Ljava/util/Map;",
                "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Class<*>;>;",
                null)
        methodVisitor.visitCode()
        // 3.1 创建一个Map
        methodVisitor.visitTypeInsn(NEW, "java/util/HashMap")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false)
        // 保存map到var0变量
        methodVisitor.visitVarInsn(ASTORE, 0)
        //  3.2 放入所有映射表
        classNames.each { String className ->
            // 拿到var0变量
            methodVisitor.visitVarInsn(ALOAD, 0)
            methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    className,
                    "get",
                    "()Ljava/util/Map;",
                    false)
            methodVisitor.visitMethodInsn(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "putAll",
                    "(Ljava/util/Map;)V",
                    true)
        }

        // 拿到var0变量并返回var0
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(2, 1)
        methodVisitor.visitEnd()

        return classWriter.toByteArray()
    }
}