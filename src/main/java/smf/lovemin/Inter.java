package smf.lovemin;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Inter {
    public static int inter;

    public static void PreCheck() {
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "default.json";
            String tempFilePath = System.getProperty("user.dir") + File.separator + "temp";
            File tempDir = new File(tempFilePath);
            if (!tempDir.exists()) {
                tempDir.mkdir(); // 如果temp文件夹不存在，则创建它
            }
            Path defaultJsonPath = Paths.get(stringUrl);
            if (Files.exists(defaultJsonPath)) {
                JSONObject jsonObject = JSONObject.parse(Files.readString(defaultJsonPath));
                if (
                        !jsonObject.containsKey("PropInfo") ||
                                !jsonObject.getString("PropInfo").equals("RSBWorkerPropertySheet") ||
                                !jsonObject.containsKey("AuthorInfo") ||
                                !jsonObject.getJSONObject("AuthorInfo").containsKey("Developer") ||
                                !jsonObject.getJSONObject("AuthorInfo").getString("Developer").equals("SMF")
                ) {
                    throw new Exception("配置文件异常，程序结束");
                }
            }
        } catch (Exception e) {
            System.out.println("配置文件异常:");
            e.printStackTrace();
        }
    }

    public static String getTestBy() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "default.json";
            Path defaultJsonPath = Paths.get(stringUrl);
            JSONObject jsonObject = JSONObject.parse(Files.readString(defaultJsonPath));
            JSONArray jsonArray = jsonObject.getJSONObject("AuthorInfo").getJSONArray("TestBy");
            for (int i = 0; i < jsonArray.size(); i++) {
                String s = jsonArray.getString(i);
                stringBuilder.append(s);
                if (i < jsonArray.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
        } catch (Exception e) {
            System.out.println("配置文件异常:");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void setGlobalSetting() {
        inter = getGlobalSetting("Inter") == null ? inter() : Integer.parseInt(Objects.requireNonNull(getGlobalSetting("Inter")).toString());
    }

    public static Object getGlobalSetting(String key) {
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "default.json";
            Path defaultJsonPath = Paths.get(stringUrl);
            JSONObject jsonObject = JSONObject.parse(Files.readString(defaultJsonPath));
            JSONObject GlobalSettings = jsonObject.getJSONObject("PropData").getJSONObject("GlobalSettings");
            return GlobalSettings.getJSONObject(key).get("value");
        } catch (Exception e) {
            return null;
        }
    }

    private static int inter() {
        int inter = 0;
        boolean keepRunning = true;
        System.out.println("""
                功能列表：
                [1] Resources.json版本切换
                [0] 退出程序
                请输入功能序号并按回车键继续……：
                """);
        while (keepRunning) {
            inter = smfScanner.smfInt(false);
            switch (inter) {
                case 1:
                    System.out.println("[1] Resources.json版本切换");
                    keepRunning = false;
                    break;
                case 0:
                    System.out.println("[0] 退出程序");
                    keepRunning = false;
                default:
                    System.out.println("\033[31m" + "输入无效，请重新输入功能序号：" + "\033[0m");
            }
        }
        System.out.println("\n————————————————————————————————————————————————————————————————————————————");
        return inter;
    }
}
