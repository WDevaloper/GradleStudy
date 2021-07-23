package com.github.router.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project


class TestIncrementalTransform extends IncrementalTransform {

    TestIncrementalTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean doJarAction(InputStream inputStream, OutputStream outputStream) {
        println("DefaultIncrementalTransform.doJarAction ${Thread.currentThread().name}")
        return false

    }

    @Override
    protected boolean doDirectoryAction(File inputJar, File outputJar) {
        println("DefaultIncrementalTransform.doDirectoryAction ${Thread.currentThread().name}")
        return false
    }

    @Override
    String getName() {
        return "TestIncrementalTransform"
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
        return true
    }
}