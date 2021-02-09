<html>

<head>
    <meta charset="utf-8">
    <title>修改员工</title>
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
        <div class="form-group">
            <label>按照格式编辑员工信息</label>
            <a href="https://www.bejson.com/" target="_Blank"> (点击使用json辅助工具)</a>
            <textarea id="employee" class="form-control" rows="20">${employee!}</textarea>
        </div>
    </div>
        <div class="row clearfix">
            <div class="col-md-4 column">
            </div>
            <div class="col-md-4 column">
            </div>
            <div class="col-md-4 column">
                <button type="button" class="btn btn-lg btn-block btn-success" onclick="save()">保存</button>
            </div>
        </div>

</div>

</body>

</html>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
    function save() {
        $.ajax({
            type: 'POST',
            url: "/daily/employee/save",
            data: {"employee":$("#employee").val()},
            error: function(data){
                alert("ajax提交失败");
            },
            success: function(data){
                alert(data)
            }
        })
    }
</script>