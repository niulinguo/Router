package com.lingo.router.gradle

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RouterMappingByteCodeBuilder implements Opcodes {
    public static final String CLASS_NAME = "com/lingo/router/mapping/generated/RouterMapping"

    private static void writeClassName(ClassWriter cw) {
        cw.visit(
                V1_8,
                ACC_PUBLIC | ACC_SUPER,
                CLASS_NAME,
                null,
                "java/lang/Object",
                null
        )
    }

    private static void writeClassConstructionMethod(ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        )

        mv.visitCode()
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(
                INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false
        )
        mv.visitInsn(RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    private static void writeClassGetMethod(ClassWriter cw, Set<String> allMappingNames) {
        final MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC,
                "get",
                "()Ljava/util/HashMap;",
                "()Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;",
                null
        )

        mv.visitCode()
        mv.visitTypeInsn(NEW, "java/util/HashMap")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(
                INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false
        )
        mv.visitVarInsn(ASTORE, 0)
        allMappingNames.each {
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(
                    INVOKESTATIC,
                    "com/lingo/router/mapping/$it",
                    "get",
                    "()Ljava/util/Map;",
                    false
            )
            mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/util/HashMap",
                    "putAll",
                    "(Ljava/util/Map;)V",
                    false
            )
        }
        mv.visitVarInsn(ALOAD, 0)
        mv.visitInsn(ARETURN)
        mv.visitMaxs(2, 1)
        mv.visitEnd()
    }

    static byte[] getByteCode(Set<String> allMappingNames) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        writeClassName(cw)
        writeClassConstructionMethod(cw)
        writeClassGetMethod(cw, allMappingNames)
        cw.visitEnd()
        return cw.toByteArray()
    }
}