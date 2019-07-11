package org.lili.forfun.shark.util;

import java.io.File;
import java.io.IOException;

public final class FileUtil {

    private FileUtil() {
    }

    /**
     * 输出func测试的结果目录
     *
     * @param dir
     * @return
     */
    public static String getResultOutDir(String dir) {
        File file0 = new File(".");
        String dirPath = null;
        try {
            dirPath = file0.getCanonicalPath() + File.separator + dir;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dirPath;
    }
}
