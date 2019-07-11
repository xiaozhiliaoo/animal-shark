package org.lili.forfun.shark.analyzer;

import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.io.File;

import static org.lili.forfun.shark.util.DateUtil.makeResultDir;


public class FolderManager {

    private String indetity;

    private FolderManager() {
        indetity = makeResultDir();
    }

    private static FolderManager folderManager;

    @Setter
    private String inputFile;

    private String outputDir = "./dialog-result";

    public static FolderManager getInstance() {
        if (folderManager == null) {
            folderManager = new FolderManager();
        }
        return folderManager;
    }

    /**
     * 设置输入文件路径
     *
     * @param outputDir
     */
    public void setOutputDir(String outputDir) {
        if (!Strings.isEmpty(outputDir)) {
            this.outputDir = outputDir;
        }
    }

    public String getOutputDir() {
        String separator = File.separator;
        if (!Strings.isEmpty(outputDir)) {
            outputDir = outputDir + separator;
        }
        //makeResultDir() 创建以年_月为主目录结构
        File file = new File(outputDir + indetity + separator);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath() + separator;
    }

    public File[] getFuncReport() {
        String separator = File.separator;
        File file = new File(outputDir + indetity + separator);
        return file.listFiles(item -> item.getName().endsWith(".html"));
    }
}
