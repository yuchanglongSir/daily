package com.yxd.daily.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yxd.daily.dao.DBUtil;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * @author jiapeng
 * @describe 所有的Controller
 */
@RestController
@RequestMapping("/daily")
public class AllController {

    /**
     * 获取编辑员工页面
     * @return
     */
    @GetMapping("/employee_")
    public ModelAndView getEmployeePage(Map<String, Object> map) {

        String re;
        try {
           re = DBUtil.getEmployee();
        } catch (Exception e) {
            re = e.getMessage();
        }

        map.put("employee", re);

        return new ModelAndView("employee", map);
    }

    /**
     * 保存员工信息 接口
     * @param employee
     * @return
     */
    @PostMapping("/employee/save")
    public String saveEmployee(String employee) throws Exception {

        try{
            //检查格式
            JSONObject.parseObject(employee).getJSONArray("all");
        } catch (Exception e) {
            return "json格式错误";
        }

        try{
            //存储数据
            DBUtil.saveEmployee(employee);

        } catch (Exception e) {
            e.printStackTrace();
            return "存储失败：message="+e.getMessage()+" cause="+e.getCause()+" stackTrace="+e.getStackTrace();
        }

        return "操作成功,增加/删除/修改后，请手动修改当天的daily-文件(或删除daily后，所有人重新提交当天的日报).";
    }

    /**
     * 获取输入日报的页面
     * @param map
     * @return
     * @throws Exception
     */
    @GetMapping("/input")
    public ModelAndView getDailyPage(Map<String, Object> map) throws Exception {

        JSONObject json = JSONObject.parseObject(DBUtil.getEmployee());

        //姓名 组名
        map.put("nameGroup", json.toString());

        //日期
        map.put("day", LocalDate.now());

        //日报内容
        map.put("daily", DBUtil.getDaily("now"));


        //用户量
        JSONArray jsonArray = json.getJSONArray("all");
        int count = 0;
        for(int i=0; i<jsonArray.size(); i++) {
            count += jsonArray.getJSONObject(i).getString("member").split(",").length;
        }
        map.put("userNum", count);

        //第几周
        Calendar c = Calendar.getInstance();
        int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        map.put("weekOfYear", weekOfYear);


        return new ModelAndView("daily");
    }

    /**
     * 获取日报记录的页面
     * @return
     * @throws Exception
     */
    @GetMapping("/week")
    public ModelAndView getAllDaily(Map<String, Object> map,String name) throws Exception {
        String history = DBUtil.getAllDaily(name);
        map.put("name",name);
        map.put("history",history);
        return new ModelAndView("history");
    }

        /**
         * 保存日报
         * @param daily
         * @return
         */
    @PostMapping("/save")
    public String saveDaily(String daily) throws Exception {
        //数据库中读取旧的内容
        JSONArray jsonDao = (JSONArray)JSONObject.parse(DBUtil.getDaily("now"));

        //System.out.println(jsonDao);
        JSONObject jsonold = JSONObject.parseObject(daily);
        int num = 0;//记录提交排序数
        String subtime = "";
        boolean isRun = false;//作为判断是否是未提交过然后执行计数的变量
        for(int i=0; i<jsonDao.size(); i++) {
            String tomcon = jsonDao.getJSONObject(i).getString("tomorrow");
            if(jsonDao.getJSONObject(i).getString("name").equals(jsonold.getString("name")) && jsonDao.getJSONObject(i).getString("subnum") != null && !jsonDao.getJSONObject(i).getString("subnum").isEmpty()){
                isRun = true;
                num = Integer.parseInt(jsonDao.getJSONObject(i).getString("subnum"));
                subtime = jsonDao.getJSONObject(i).getString("subtime");
            }
            if(!isRun){
                if(!tomcon.isEmpty() && !jsonDao.getJSONObject(i).getString("name").equals(jsonold.getString("name"))){
                    num+=1;
                }
            }

        }
        //System.out.println(isRun);
        if(!isRun){
            //自己当前是第几个提交
            num+=1;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            subtime = format.format(new Date());
        }
        String newstr = "";
        String []dailyArr =  daily.split(",");
        for(int i=0; i<dailyArr.length; i++) {

            int h = dailyArr[i].indexOf("\"tomorrow\":");
            int j = dailyArr[i].indexOf("\"today\":");
            int k = dailyArr[i].lastIndexOf("\"");
            if(j>0){
                String content = dailyArr[i].substring(j+10,k);
                if(content.indexOf("\"")>0){
                    content = content.replaceAll("\"","\'");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.indexOf(",")>0){
                    content = content.replaceAll(",","，");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.indexOf(";")>0){
                    content = content.replaceAll(";","；");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                String headStr = content.substring(0,2);
                if(headStr.equals("1、")){
                    content = content.replaceAll("1、","1.");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }

                if(content.indexOf("2、")>0){
                    content = content.replaceAll("2、","2.");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.indexOf("3、")>0){
                    content = content.replaceAll("3、","3.");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.indexOf("4、")>0){
                    content = content.replaceAll("4、","4.");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.indexOf("5、")>0){
                    content = content.replaceAll("5、","5.");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.toLowerCase().indexOf("sql")>0){
                    content = content.toLowerCase().replaceAll("sql","SQL");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }
                if(content.toLowerCase().indexOf("bug")>0){
                    content = content.toUpperCase().replaceAll("BUG","bug");
                    dailyArr[i] = "\"today\":\""+content+"\"";
                }

            }
            if(h>0){
                String content = dailyArr[i].substring(h+13,k);
                if(content.indexOf("\"")>0){
                    content = content.replaceAll("\"","\'");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.indexOf(",")>0){
                    content = content.replaceAll(",","，");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.indexOf(";")>0){
                    content = content.replaceAll(";","；");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                String headStr = content.substring(0,2);
                if(headStr.equals("1、")){
                    content = content.replaceAll("1、","1.");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }

                if(content.indexOf("2、")>0){
                    content = content.replaceAll("2、","2.");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.indexOf("3、")>0){
                    content = content.replaceAll("3、","3.");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.indexOf("4、")>0){
                    content = content.replaceAll("4、","4.");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.indexOf("5、")>0){
                    content = content.replaceAll("5、","5.");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.toLowerCase().indexOf("sql")>0){
                    content = content.toLowerCase().replaceAll("sql","SQL");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                if(content.toLowerCase().indexOf("bug")>0){
                    content = content.toUpperCase().replaceAll("BUG","bug");
                    dailyArr[i] = "\"tomorrow\":\""+content+"\"";
                }
                int l = dailyArr[i].lastIndexOf("}");
                if(l<0){
                    dailyArr[i]+="}";
                }
            }
            if(i!=dailyArr.length-1){
                newstr += dailyArr[i]+",";
            }else{
                newstr+="\"subnum\":\""+num+"\"";
                newstr+=",";
                newstr+="\"subtime\":\""+subtime+"\"";
                newstr+=",";
                newstr += dailyArr[i];
            }
        }
        //System.out.println(newstr);
        //提交的内容(一条信息)
        JSONObject json = JSONObject.parseObject(newstr);

        if("请选择".equals(json.getString("name"))) {
            return "提交失败，我不相信你叫'请选择'！😁";
        }


        for(int i=0; i<jsonDao.size(); i++) {
            if(jsonDao.getJSONObject(i).getString("name").equals(json.getString("name"))) {
                jsonDao.set(i, json);
            }
        }

        DBUtil.saveDaily(jsonDao.toString());

        return "提交成功.";
    }

    /**
     * 获取Excel文件
     * @param response
     * @throws Exception
     */
    @GetMapping("/excel/{d}")
    public void downloadExcel(HttpServletResponse response,@PathVariable String d) {

        // 创建工作流
        OutputStream os = null;

        //
        WritableWorkbook book = null;

        try {

            //顶部字体字体
            WritableFont wf = new WritableFont(WritableFont.TIMES,10,WritableFont.BOLD,false);
            //文字对齐方式
            WritableCellFormat wcf = new WritableCellFormat(wf);
            //把水平对齐方式指定为居中
            wcf.setAlignment(Alignment.CENTRE);
            //把垂直对齐方式指定为居中
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
            //设置边框
            wcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

            //左侧字体
            WritableFont wf2 = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,false);
            //文字对齐方式
            WritableCellFormat wcf2 = new WritableCellFormat(wf2);
            //把水平对齐方式指定为居中
            wcf2.setAlignment(Alignment.CENTRE);
            //把垂直对齐方式指定为居中
            wcf2.setVerticalAlignment(VerticalAlignment.CENTRE);
            //设置边框
            wcf2.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

            //正文内容字体
            WritableFont wf3 = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,false);
            //文字对齐方式
            WritableCellFormat wcf3 = new WritableCellFormat(wf3);
            //把水平对齐方式指定为居左
            wcf3.setAlignment(Alignment.LEFT);
            //把垂直对齐方式指定为居上
            wcf3.setVerticalAlignment(VerticalAlignment.TOP);
            //自动换行
            wcf3.setWrap(true);
            //设置边框
            wcf3.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);


            //自动设置大小
            CellView navCellView = new CellView();
            navCellView.setAutosize(true);

            // 创建工作表
            response.reset();

            // 设置字符集
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream; charset=utf-8");

            String filenamedate = "";
            if("now".equals(d)){
                filenamedate = LocalDate.now().toString();
            }else{
                filenamedate = LocalDate.now().plusDays(-1).toString();
            }
            // 设置工作表的标题
            response.setHeader("Content-Disposition", "attachment; filename=daily-"+filenamedate+".xls");
            os = response.getOutputStream();

            // 初始化工作表
            book = Workbook.createWorkbook(os);

            //内容
            WritableSheet sheet = book.createSheet("工作日报", 0);

            //设置表头
            sheet.addCell(new Label(0, 0, "序号", wcf));
            //设置宽度
            sheet.setColumnView(0, 8);
            sheet.addCell(new Label(1, 0, "工作组", wcf));
            sheet.setColumnView(1, 10);
            sheet.addCell(new Label(2, 0, "姓名", wcf));
            sheet.setColumnView(2, 10);
            sheet.addCell(new Label(3, 0, "今日工作内容", wcf));
            sheet.setColumnView(3, 60);
            sheet.addCell(new Label(4, 0, "存在的问题与争议", wcf));
            sheet.setColumnView(4, 30);
            sheet.addCell(new Label(5, 0, "明日工作计划", wcf));
            sheet.setColumnView(5, 60);

            //获取日报内容
            JSONArray jsonArray;
            if("now".equals(d)){
                jsonArray = JSONArray.parseArray(DBUtil.getDaily(d));
            }else{
                jsonArray = JSONArray.parseArray(DBUtil.getDaily("yesterday"));
            }


            //统计总人数
            int count = jsonArray.size();

            //获取每组人数
            List<String> groupName = new ArrayList<>();
            List<Integer> groupNum = new ArrayList<>();
            JSONArray empArr = JSONObject.parseObject(DBUtil.getEmployee()).getJSONArray("all");

            int empCount = empArr.size();
            for(int i =0; i<empCount; i++) {
                groupName.add(empArr.getJSONObject(i).getString("groupName"));
                groupNum.add(empArr.getJSONObject(i).getString("member").split(",").length);
            }

            //填写日报
            int old = 0;
            for (int i = 1,u = 0, j = 0; i < count + 1; i++) {

                //第i个人的日报
                JSONObject member = jsonArray.getJSONObject(i-1);

                // 序号从1开始
                sheet.addCell(new Label(0, i, String.valueOf(i), wcf2));
                //工作组
                u++;
                if(u == 1) {
                    sheet.addCell(new Label(1, i, groupName.get(j), wcf2));
                    //姓名
                    sheet.addCell(new Label(2, i, member.getString("name"), wcf));
                } else {
                    //姓名
                    sheet.addCell(new Label(2, i, member.getString("name"), wcf2));
                }
                if(u == groupNum.get(j)) {
                    sheet.mergeCells(1, old+1, 1, i);
                    old = i;
                    u = 0;
                    j++;
                }

                //今日工作内容
                sheet.addCell(new Label(3, i, member.getString("today"), wcf3));
                //存在的问题与建议
                sheet.addCell(new Label(4, i, member.getString("evaluation"), wcf3));
                //明日工作计划
                sheet.addCell(new Label(5, i, member.getString("tomorrow"), wcf3));

            }

            book.write();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if(null != book) {
                try {
                    book.close();
                } catch (IOException e) {
                    System.out.println("book.close();   IOException");
                    e.printStackTrace();
                } catch (WriteException e) {
                    System.out.println("book.close();   WriteException");
                    e.printStackTrace();
                }
            }

            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    System.out.println("os.close();");
                    e.printStackTrace();
                }
            }
        }

    }

}
