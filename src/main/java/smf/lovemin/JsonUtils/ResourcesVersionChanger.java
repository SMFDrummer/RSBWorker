package smf.lovemin.JsonUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.lovemin.smfScanner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

import static smf.lovemin.JsonUtils.Base.getFilePath;

public class ResourcesVersionChanger {
    public static void measure(){
        String resFilePath = getFilePath("请拖入Resources.json文件，或输入路径，并按回车继续……");
        File file = new File(resFilePath);
        try {
            JSONObject parse = JSON.parseObject(Files.readString(file.toPath()));
            boolean isNew = parse.containsKey("content_version");
            JSONArray groups = parse.getJSONArray("groups");
            Iterator<Object> objectGroups = groups.iterator();
            int groupsIndex = 0;
            while (objectGroups.hasNext()){
                JSONObject groupElement = (JSONObject) objectGroups.next();
                if (groupElement.containsKey("resources")){
                    JSONArray resources = groupElement.getJSONArray("resources");
                    Iterator<Object> objectResources = resources.iterator();
                    int resourcesIndex = 0;
                    while (objectResources.hasNext()){
                        JSONObject pathElement = (JSONObject) objectResources.next();
                        if (isNew){
                            if (pathElement.containsKey("path") && !JSON.isValidArray(pathElement.getString("path"))){
                                resources.set(resourcesIndex,pathChanger(pathElement));
                            }
                        } else {
                            if (pathElement.containsKey("path") && JSON.isValidArray(pathElement.getString("path"))){
                                resources.set(resourcesIndex,pathChanger(pathElement));
                            }
                        }
                        resourcesIndex++;
                    }
                    groups.set(groupsIndex,groupElement);
                }
                groupsIndex++;
            }
            parse.put("groups",groups);
            if (isNew){
                parse.remove("content_version");
            } else {
                parse.put("content_version",1);
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(parse.toJSONString(JSONWriter.Feature.PrettyFormat,JSONWriter.Feature.WriteMapNullValue));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject pathChanger(JSONObject pathElement) {
        if (JSON.isValidArray(pathElement.getString("path"))) {
            JSONArray pathArray = pathElement.getJSONArray("path");
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<Object> object = pathArray.iterator();
            int objectIndex = 0;
            while (object.hasNext()){
                String pathArrayElement = (String) object.next();
                stringBuilder.append(pathArrayElement);
                if (objectIndex != pathArray.size() - 1){
                    stringBuilder.append("\\");
                }
                objectIndex++;
            }
            pathElement.put("path",stringBuilder.toString());
        } else {
            String[] pathStringElements = pathElement.getString("path").split("\\\\");
            JSONArray pathArray = new JSONArray();
            pathArray.addAll(Arrays.asList(pathStringElements));
            pathElement.put("path",pathArray);
        }
        return pathElement;
    }

}
