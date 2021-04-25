package com.github.router.core

import java.util.jar.JarEntry
import java.util.jar.JarFile


class RouterMappingCollector {
    // 注解处理器生成映射表类的特征
    private final String MAPPING_PACKAGE_NAME = "com/github/gradle/mapping"
    private final String MAPPING_CLASS_SUFFIX = "\$\$Module\$\$RouterMapping"
    private final String CLASS_FILE_SUFFIX = ".class"

    private final Set<String> mappingClassNames = new HashSet<>()

    /**
     *
     * @return 获取映射表的类名
     */
    Set<String> getMappingClassNames() {
        return mappingClassNames
    }

    /**
     * 收集class文件或class目录中的映射表目标类
     *
     * @param classFile
     */
    void collect(File classFile) {
        if (classFile == null || !classFile.exists()) {
            return
        }
        if (classFile.isFile()) {
            // classFile.absolutePath  >>> build\intermediates\javac\debug\classes\com\github\gradle\mapping\app$$Module$$RouterMapping.class
            // classFile.name >>>>> app$$Module$$RouterMapping.class
            if (classFile.name.endsWith(MAPPING_CLASS_SUFFIX + CLASS_FILE_SUFFIX)) {
                String className = classFile.name.replace(CLASS_FILE_SUFFIX, "")
                mappingClassNames.add(MAPPING_PACKAGE_NAME + "/" + className)
            }
            return
        }

        File[] childFiles = classFile.listFiles()

        if (childFiles == null || childFiles.length <= 0) {
            return
        }

        childFiles.each { File file -> collect(file) }
    }

    /**
     * 收集Jar中的映射目标类
     *
     * @param classFile
     */
    void collectFromJarFile(File file) {
        Enumeration<JarEntry> entries = new JarFile(file).entries()

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement()
            // jarEntry.name >>>>>> com/github/gradle/mapping/hotfit$$Module$$RouterMapping.class
            if (jarEntry.name.endsWith(MAPPING_CLASS_SUFFIX + CLASS_FILE_SUFFIX)) {
                String className = jarEntry.name.replace(CLASS_FILE_SUFFIX, "")
                mappingClassNames.add(className)
            }
        }
    }
}