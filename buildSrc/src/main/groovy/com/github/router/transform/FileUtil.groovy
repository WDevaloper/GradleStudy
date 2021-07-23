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

    static File toOutputFile(File outputDir, File inputDir, File inputFile) {
        return new File(outputDir, FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir))
    }


}