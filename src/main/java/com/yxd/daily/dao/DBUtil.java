package com.yxd.daily.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author jiapeng
 * @describe Dao工具
 */
public class DBUtil {

    /**
     * 员工文件路径
     */
    static final String EMPLOYEE_PATH = "./employee";

    /**
     * 日报文件前缀
     */
    static final String DAILY_PATH_PREFIX = "./daily-";

    /**
     * 文件夹路径
     */
    public static final String DIR_PATH = "./";

    static final String DAILY_2 = "daily-2";

    /**
     * 存储员工信息
     * @param employee
     * 参数格式：
            {
            "all": [{
            "groupName": "数据组",
            "member": "超哥,大鹏✌️,于长龙,李胜"
            }, {
            "groupName": "资审组",
            "member": "岩哥,刘镇,孙琪"
            }]
            }
     */
    public synchronized static void saveEmployee(String employee) throws Exception{

        //移除所有空格
        employee = employee.replaceAll(" ", "");
        //移除所有tab
        employee = employee.replaceAll("\t", "");

        saveFile(EMPLOYEE_PATH, employee);

    }

    /**
     * 读取员工信息
     * @return
     */
    public synchronized static String getEmployee() throws Exception{

        return readFile(EMPLOYEE_PATH);

    }

    /**
     * 获取昨天的文件
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    public synchronized static String getYesterday(String fileName) throws Exception {
        return readFile(DIR_PATH+fileName);
    }

    /**
     * 获取当前天数(每天对应一个 唯一字符串)
     * @return
     */
    private synchronized static String getDay() {
        return LocalDate.now().toString();
    }

    /**
     * 获取前一天天数(每天对应一个 唯一字符串)
     * @return
     */
    private synchronized static String getYDay() {
        return LocalDate.now().plusDays(-1).toString();
    }

    /**
     * 保存文件方法(java8)
     * @param pathStr
     * @param context
     * @throws Exception
     */
    private synchronized static void saveFile (String pathStr, String context) throws Exception {
        Path path = Paths.get(pathStr);
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        try
        {
            writer.write(context);
        }finally {
            writer.close();
        }

    }

    /**
     * 读取文件的方法(java8)
     * @param pathStr
     * @return
     * @throws Exception
     */
    private synchronized static String readFile(String pathStr) throws Exception {
//        StringBuffer sb = new StringBuffer();
//        Files.lines(Paths.get(pathStr), StandardCharsets.UTF_8).forEach(line -> sb.append(line));
//        return sb.toString();
        StringBuffer sb = new StringBuffer();
        try(Stream<String> stream = Files.lines(Paths.get(pathStr),StandardCharsets.UTF_8)){
            stream.forEach(line -> sb.append(line));
        } catch (IOException e){
            System.out.print("Exception:"+e.getMessage());
        }
        return sb.toString();
    }

//    /**
//     * 检查今日提交的日报并返回已提交的数量
//     * @return
//     * @throws Exception
//     */
//    public synchronized static int getSubmitNum()throws Exception{
//        int num = 0;
//
//        String day = getDay();
//        String dayFileName = DAILY_PATH_PREFIX+day;
//        File file=new File(dayFileName);
//        if(file.exists()) {
//            String content =  readFile(dayFileName);
//            JSONArray array = JSON.parseArray(content);
//            JSONObject jsonContent;
//            for(int index=0; index<array.size(); index++) {
//                jsonContent = array.getJSONObject(index);
//                System.out.print(jsonContent);
//            }
//
//        }
//        return num;
//    }

    /**
     * 获取日报内容
     * @return
     * @throws Exception
     */
    public synchronized static String getDaily(String d) throws Exception {

        String day = "";
        if("now".equals(d)){
            day = getDay();
        }else{
            day = getYDay();
        }

        String dayFileName = DAILY_PATH_PREFIX+day;

        //如果文件不存在 创建文件 (可改进为 使用java8新类库)
        File file=new File(dayFileName);
        if(!file.exists()) {

            //读取旧的文件，获取 "明日计划"
            File oldFile = new File(DIR_PATH);
            //将该目录下的所有文件放置在一个File类型的数组中
            File[] fileListAll = oldFile.listFiles();

            //过滤
            List<File> oldFileList = new ArrayList<>();
            for(File currFile : fileListAll) {
                if(currFile.isFile() && currFile.getName().startsWith(DAILY_2)) {
                    oldFileList.add(currFile);
                }
            }

            //创建
            file.createNewFile();

            //搜索最后一天的文件
            File yesterday = null;
            if(oldFileList.size() > 0) {
                yesterday = oldFileList.get(0);
                if(oldFileList.size() > 1) {
                    //寻找最后一天的
                    for (int i = 0; i < oldFileList.size(); i++) {
                        //寻找最大文件
                        if(oldFileList.get(i).compareTo(yesterday) > 0) {
                            yesterday = oldFileList.get(i);
                        }
                    }
                }
            }

            //获取昨天的文件
            JSONArray yesterdayArr = null;
            if(yesterday != null) {
                yesterdayArr = JSONObject.parseArray(getYesterday(yesterday.getName()));
            }



            JSONObject dailyInitJson = JSONObject.parseObject(DBUtil.getEmployee());
            JSONArray employeeArr = dailyInitJson.getJSONArray("all");

            JSONArray jsonArray = new JSONArray();
            JSONObject memberJson;

            JSONObject jsonGroup;
            for(int index=0; index<employeeArr.size(); index++) {
                jsonGroup = employeeArr.getJSONObject(index);
                String[] members = ((String)jsonGroup.get("member")).split(",");

                for(String member : members) {
                    memberJson = new JSONObject();
                    memberJson.put("name", member);

                    //今日工作内容
                    // 昨天的明日计划带过来
                    if(yesterdayArr != null) {
                        for(int i=0; i< yesterdayArr.size(); i++) {

                            JSONObject json = yesterdayArr.getJSONObject(i);
                            if(member.equals(json.getString("name"))) {
                                memberJson.put("today", json.getString("tomorrow"));
                                break;
                            }
                        }
                    } else {
                        memberJson.put("today", "");
                    }

                    //存在的问题与建议:默认无
                    memberJson.put("evaluation", "无");

                    //明日计划
                    memberJson.put("tomorrow", "");

                    jsonArray.add(memberJson);
                }

            }

            //初始化文件
            saveFile(dayFileName, jsonArray.toString());

            return jsonArray.toString();

        }

        return readFile(dayFileName);
    }

    /**
     * 获取所有文件 of name
     * @return
     * @throws Exception
     */
    public synchronized static String getAllDaily(String name) throws Exception {

        StringBuffer sb = new StringBuffer();

        //读取旧的文件
        File oldFile = new File(DIR_PATH);
        //将该目录下的所有文件放置在一个File类型的数组中
        File[] fileListAll = oldFile.listFiles();

        //过滤
        List<File> oldFileList = new ArrayList<>();
        for(File currFile : fileListAll) {
            if(currFile.isFile() && currFile.getName().startsWith(DAILY_2)) {
                oldFileList.add(currFile);
            }
        }

        //排序
        oldFileList.sort(
                new Comparator<File>() {
                    @Override
                    public int compare(File file1, File file2) {
                        return -file1.getName().compareTo(file2.getName());
                    }
                }
        );

        //读取所有的
        String subtime = "";
        for(File file : oldFileList) {
            JSONArray jsonArray = JSONObject.parseArray(getYesterday(file.getName()));
            for(int i=0; i<jsonArray.size(); i++) {
                if(name.equals(jsonArray.getJSONObject(i).getString("name"))) {

                    if(jsonArray.getJSONObject(i).getString("subtime") == null || jsonArray.getJSONObject(i).getString("subtime").isEmpty()){
                        subtime = "";
                    }else{
                        subtime = jsonArray.getJSONObject(i).getString("subtime");
                    }
                    sb.append(file.getName().substring(6)+"：\n" +
                            "提交时间：\n" + subtime +"\n" +
                            "今日计划：\n" + jsonArray.getJSONObject(i).getString("today") +"\n" +
                    "明日计划：\n" + jsonArray.getJSONObject(i).getString("tomorrow") + "\n\n");
                }
            }
        }

        return sb.toString();

    }

    /**
     * 保存日报内容
     * @throws Exception
     */
    public synchronized static void saveDaily(String daily) throws Exception {

        String day = getDay();

        saveFile(DAILY_PATH_PREFIX+day, daily);
    }



}
