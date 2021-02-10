<html>

<head>
    <meta charset="utf-8">
    <title>填写日报</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>

<div class="container">

    <#--标题-->
    <div class="row clearfix">
        <div class="col-md-12 column">
            <h3 class="text-center">
                日报提交服务v1.0
            </h3>
        </div>
    </div>

    <br/>
    <br/>

    <div class="row clearfix">
        <div class="col-md-2 column">
            <label>姓名：</label>
            <select id="nameSelectId">
                <option value ="请选择">请选择</option>
            </select>
        </div>
        <div class="col-md-2 column">
            <label>工作组：</label>
            <label id="groupId">请选择</label>
        </div>
        <div class="col-md-2 column">
            <label>日期：${day}</label>
        </div>
        <div class="col-md-3 column hide">
            <label>提交时间：<span id="firstSubTime"></span></label>
        </div>
        <div class="col-md-3 column hide">
            <label style="color: red;">第<span id="subNum"></span>个提交</label>
        </div>
    </div>

    <br/>

    <div class="row clearfix">
        <div class="form-group">
            <label>今日工作内容</label>
            <textarea id="today" class="form-control" rows="3" onKeyUp="ReplaceDot(this)" placeholder="1.这是一个测试；&#10;2.这是一个测试。&#10;规则如上面示例：阿拉伯数字+英文输入下的点(.)开头,以中文输入下的分号(；)做间隔,中文输入下的句号(。)结尾。请大家严格按照示例填写日报！！！"></textarea>
        </div>
    </div>

    <div class="row clearfix">
        <div class="form-group">
            <label>存在的问题与建议</label>
            <textarea id="evaluation" class="form-control" rows="3"></textarea>
        </div>
    </div>

    <div class="row clearfix">
        <div class="form-group">
            <label>明日工作计划</label>
            <textarea id="tomorrow" class="form-control" rows="3" onKeyUp="ReplaceDot(this)" placeholder="1.这是一个测试；&#10;2.这是一个测试。&#10;规则如上面示例：阿拉伯数字+英文输入下的点(.)开头,以中文输入下的分号(；)做间隔,中文输入下的句号(。)结尾。请大家严格按照示例填写日报！！！"></textarea>
        </div>
    </div>

    <div class="row clearfix">
        <div class="col-md-3 column">
            <a href="/daily/excel/now">
                <button type="button" class="btn btn-lg btn-block btn-success">导出今日Excel</button>
            </a>
        </div>

        <div class="col-md-3 column">
             <button type="button" class="btn btn-lg btn-block btn-primary" onclick="history()">查看提交记录</button>
        </div>
        <div class="col-md-3 column">
            <button type="button" class="btn btn-lg btn-info btn-block" onclick="save()">无冲突提交</button>
        </div>
        <div class="col-md-3 column">
            <a href="/daily/excel/yesterday">
                <button type="button" class="btn btn-lg btn-block btn-success" style="background-color: red;border-color: red;">导出昨日Excel</button>
            </a>
        </div>
    </div>

        <div class="row clearfix">
            <div class="col-md-12 column">
                <h4>
                    本周是今年第 ${weekOfYear} 周
                </h4>
                <h4>
                    当前用户量：${userNum} 人
                </h4>

                <p align="right">
                    —— 于长龙.
                </p>
                <p align="right">
                    最后更新时间:2019-04-18
                </p>
            </div>
        </div>

</div>

</body>

</html>

<script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
<script type="text/javascript">

    //回显消息 的 数据
    var daily = ${daily!}

    //选择姓名时，修改组名称
    $("#nameSelectId").change(function(){

        //设置组
        var group = $(this).children('option:selected').val();
        $("#groupId").html(group);

        //获取选择的姓名
        var name = $(this).children('option:selected').text();

        for(var i=0; i<daily.length; i++) {
            var oneDaily = daily[i];
            if(oneDaily.name == name) {
                $("#today").val(oneDaily.today)
                $("#evaluation").val(oneDaily.evaluation)
                $("#tomorrow").val(oneDaily.tomorrow)
                if(oneDaily.subtime){
                    $("#firstSubTime").html(oneDaily.subtime)
                    $("#firstSubTime").parent().parent().removeClass("hide");
                }else{
                    $("#firstSubTime").html("");
                    $("#firstSubTime").parent().parent().addClass("hide");
                }
                if(oneDaily.subnum){
                    $("#subNum").html(oneDaily.subnum)
                    $("#subNum").parent().parent().removeClass("hide");
                }else{
                    $("#subNum").html("");
                    $("#subNum").parent().parent().addClass("hide");
                }
            }
        }

    });

    //给select元素加入选项
    var nameGroup = ${nameGroup};
    var all = nameGroup.all;
    for(var i=0; i<all.length; i++) {
        var groupName = all[i].groupName;
        var members = all[i].member.split(",");
        for(var j=0; j<members.length; j++) {
            $("#nameSelectId").append(new Option(members[j], groupName));
        }
    }

    function save() {

        var name = $("#nameSelectId").find("option:selected").text();
        var today = $("#today").val();
        var evaluation = $("#evaluation").val();
        var tomorrow = $("#tomorrow").val();
        var obj = '{"name":"'+name+'", "today": "'+today+'", "evaluation":"'+evaluation+'", "tomorrow": "'+tomorrow+'"}';

        //不能整体提交，会覆盖
//        var obj = {"name":name, "today": today, "evaluation": evaluation, "tomorrow": tomorrow}

//        for(var i=0; i<daily.length; i++) {
//            var oneDaily = daily[i];
//            if(oneDaily.name == name) {
////                daily[i].today = today;
////                daily[i].evaluation = evaluation;
////                daily[i].tomorrow = tomorrow;
//                daily[i] = obj;
//            }
//        }

        $.ajax({
            type: 'POST',
            url: "/daily/save",
            data: {"daily": obj},
            error: function(data){
                alert("ajax提交失败");
            },
            success: function(data){
                alert(data)
                window.location.href='/daily/input';
            }
        })
    }

    /**
     * 查看历史
     */
    function history() {
        var name = $("#nameSelectId").find("option:selected").text();
        if('请选择'==name) {
            alert("没有找到'请选择'的提交记录。");
            return;
        }
        window.location.href='/daily/week?name='+name;
    }

    function ReplaceDot(obj)
    {
        var oldValue=obj.value;
        while(oldValue.indexOf(",")!=-1)
        {
            obj.value=oldValue.replace(",","，");
            oldValue=obj.value;
        }

        while(oldValue.indexOf(";")!=-1)
        {
            obj.value=oldValue.replace(";","；");
            oldValue=obj.value;
        }
        obj.value = oldValue;
    }

</script>