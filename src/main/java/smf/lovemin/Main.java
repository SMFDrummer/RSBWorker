package smf.lovemin;
import smf.lovemin.JsonUtils.ManifestTransaction;
import smf.lovemin.JsonUtils.ResourcesVersionChanger;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Inter.PreCheck();
        String version = properties.getProperty("app.version");
        System.out.printf("""
                RSBWorker 正式版本:%s
                程序作者：SMF；协同测试：%s
                请检查 %s 目录下程序运行环境是否存在完整配置
                default.json
                
                更新日志：
                *添加功能2
                ————————————————————————————————————————————————————————————————————————————
                """,version, Inter.getTestBy(), System.getProperty("user.dir"));
        Inter.setGlobalSetting();
        switch (Inter.inter) {
            case 1 -> ResourcesVersionChanger.measure();
            case 2 -> ManifestTransaction.RunnableTransaction();
            case 0 -> System.exit(0);
            default -> {
                System.out.println("\033[31m" + "默认值非法，无法执行已知功能，请重新设置" + "\033[0m");
                System.exit(0);
            }
        }
        System.exit(0);
    }
}
