package com.github.router.transform

import com.android.utils.FileUtils


class FileUtil {
    static void copyFileAndMkdirsAsNeed(File from, File to) {
        if (!from.exists()) {
            return
        }

        to.parentFile.mkdirs()
        FileUtils.copyFile(from, to)
    }

    /**
     * 如果文件的绝对路径为a/b/c，目录为a，则此方法返回{@code bc}。
     *
     * @param outputDir
     * @param inputDir
     * @param inputFile
     * @return
     */
    static File toOutputFile(
            File outputDir, File inputDir, File inputFile) {
        return new File(
                outputDir,
                FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir)//返回截取掉 inputDir目录
        )
    }
}