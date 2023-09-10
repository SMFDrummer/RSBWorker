package smf.lovemin.JsonUtils;

import smf.lovemin.smfScanner;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Base {
    private static String removeQuotes(String str) {
        if (str != null && str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
    public static String getFilePath(String tips) {
        System.out.println(tips);
        while (true) {
            String filePath = smfScanner.smfString(false);
            filePath = removeQuotes(filePath);
            if (Files.exists(Paths.get(filePath))){
                return filePath;
            } else System.out.println("文件不存在，请重新输入");
        }
    }
}
