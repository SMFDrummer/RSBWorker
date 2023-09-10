package smf.lovemin.JsonUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.lovemin.Result;
import smf.lovemin.smfScanner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Set;

import static smf.lovemin.JsonUtils.Base.getFilePath;

public class ManifestTransaction {
    private static String manifestFile;
    private ManifestType from;
    private ManifestType to;
    private static final ManifestTransaction manifestTransaction = new ManifestTransaction();

    public void setManifestTransaction(ManifestType from,ManifestType to) {
        this.from = from;
        this.to = to;
    }
    public Result getManifestTransaction(){
        return new Result(from,to);
    }

    public enum ManifestType{
        Taiji,
        SpcUtil,
        Twinkles,
        Unknown
    }
    private static ManifestType versionCallBack(){
        manifestFile = getFilePath("请拖入清单文件，或输入路径，并按回车继续……");
        File file = new File(manifestFile);
        try {
            JSONObject parse = JSON.parseObject(Files.readString(file.toPath()));
            if (parse.containsKey("enableEmbeddedResInfo")&&parse.containsKey("enableAtlasInfoExpand")){
                return ManifestType.Taiji;
            } else if (parse.containsKey("SetResListInHeader")&&parse.containsKey("UseMoreAtlasInfo")){
                return ManifestType.SpcUtil;
            } else return ManifestType.Twinkles;
        } catch (Exception e) {
            System.out.println("清单文件存在语法错误：");
            e.printStackTrace();
            return ManifestType.Unknown;
        }
    }

    public static void RunnableTransaction(){
        switch (versionCallBack()){
            case Taiji -> {
                System.out.println("""
                        清单文件为Taiji版本，请选择转换目标：
                        [1] 转换为SpcUtil
                        [2] 转换为Twinkles
                        """);
                switch (smfScanner.smfInt(false)){
                    case 1 -> ManifestUtil(ManifestType.Taiji,ManifestType.SpcUtil);
                    case 2 -> ManifestUtil(ManifestType.Taiji,ManifestType.Twinkles);
                }
            }
            case SpcUtil -> {
                System.out.println("""
                        清单文件为SpcUtil版本，请选择转换目标：
                        [1] 转换为Taiji
                        [2] 转换为Twinkles
                        """);
                switch (smfScanner.smfInt(false)){
                    case 1 -> ManifestUtil(ManifestType.SpcUtil,ManifestType.Taiji);
                    case 2 -> ManifestUtil(ManifestType.SpcUtil,ManifestType.Twinkles);
                }
            }
            case Twinkles -> {
                System.out.println("""
                        清单文件为Twinkles版本，请选择转换目标：
                        [1] 转换为Taiji
                        [2] 转换为SpcUtil
                        """);
                switch (smfScanner.smfInt(false)){
                    case 1 -> ManifestUtil(ManifestType.Twinkles,ManifestType.Taiji);
                    case 2 -> ManifestUtil(ManifestType.Twinkles,ManifestType.SpcUtil);
                }
            }
            case Unknown -> System.out.println("你他妈把什么拖进去了？");
        }
    }
    private static void ManifestUtil(ManifestType from,ManifestType to) {
        manifestTransaction.setManifestTransaction(from,to);
        File file = new File(manifestFile);
        try {
            JSONObject parse = JSON.parseObject(Files.readString(file.toPath()));
            JSONObject parseNew = (JSONObject) processKeys(parse);

            FileWriter fileWriter = new FileWriter(manifestFile.substring(0, manifestFile.lastIndexOf("\\") + 1) + "transaction.json");
            fileWriter.write(parseNew.toJSONString(JSONWriter.Feature.PrettyFormat,JSONWriter.Feature.WriteMapNullValue));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("清单文件存在语法错误：");
            e.printStackTrace();
        }
    }
    public static Object processKeys(Object obj) {
        Result transactionMode = manifestTransaction.getManifestTransaction();
        if (obj instanceof JSONObject jsonObject) {
            JSONObject newJsonObject = new JSONObject();
            for (String key : jsonObject.keySet()) {
                String newKey = transformKey(key);
                Object value = jsonObject.get(key);
                if (newKey.equals("Res")) {
                    value = processKeys(processValue((JSONArray) value));
                } else if (newKey.equals("atlas")) {
                    value = processKeys(processValue((JSONObject) value));
                } else {
                    value = processKeys(value);
                }
                newJsonObject.put(newKey, value);
            }
            return newJsonObject;
        } else if (obj instanceof JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonArray.set(i, processKeys(jsonArray.get(i)));
            }
            return jsonArray;
        } else {
            return obj;
        }
    }
    public static JSONArray processValue(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (
                    jsonObject.getJSONArray("path").contains("UI_CALENDAR_1536_01.PTX") ||
                            jsonObject.getJSONArray("path").contains("UI_THYMEDEVENTS_1536_01.PTX") ||
                            jsonObject.getJSONArray("path").contains("WORLDMAP_EGYPT_1536_01.PTX")
            ){
                JSONObject atlas = jsonObject.getJSONObject("atlas");
                atlas.put("Idx",1);
                jsonObject.put("atlas",atlas);
            } else if (jsonObject.containsKey("atlas")) {
                JSONObject atlas = jsonObject.getJSONObject("atlas");
                atlas.put("Idx",0);
                jsonObject.put("atlas",atlas);
            }
            jsonArray.set(i, jsonObject);
        }
        return jsonArray;
    }

    public static JSONObject processValue(JSONObject jsonObject) {
        if (jsonObject.containsKey("Idx")) jsonObject.remove("Idx");
        return jsonObject;
    }

    private static String transformKey(String key) {
        return switch (key) {
            case "headerType" -> "HeaderType";
            case "group" -> "Group";
            case "composite" -> "IsComposite";
            case "subGroup" -> "SubGroup";
            case "category" -> "Category";
            case "resStoreMethod" -> "CompressMethod";
            case "res" -> "Res";
            case "path" -> "Path";
            case "atlas" -> "AtlasInfo";
            case "sz" -> "Size";
            case "fmt" -> "TexFmt";
            case "enableEmbeddedResInfo" -> "SetResListInHeader";
            case "enableAtlasInfoExpand" -> "UseMoreAtlasInfo";

            case "HeaderType" -> "headerType";
            case "Group" -> "group";
            case "IsComposite" -> "composite";
            case "SubGroup" -> "subGroup";
            case "Category" -> "category";
            case "CompressMethod" -> "resStoreMethod";
            case "Res" -> "res";
            case "Path" -> "path";
            case "AtlasInfo" -> "atlas";
            case "Size" -> "sz";
            case "TexFmt" -> "fmt";
            case "SetResListInHeader" -> "enableEmbeddedResInfo";
            case "UseMoreAtlasInfo" -> "enableAtlasInfoExpand";

            default -> key;
        };
    }

}
