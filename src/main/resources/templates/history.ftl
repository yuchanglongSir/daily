<html>

<head>
    <meta charset="utf-8">
    <title>查看历史记录</title>
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
            <label>${name} 的历史记录</label>
            <textarea id="employee" class="form-control" rows="20">${history!}</textarea>
        </div>
    </div>
        <div class="row clearfix">
            <div class="col-md-4 column">
                <button type="button" class="btn btn-lg btn-block btn-success" onclick="back()">返回</button>
            </div>
            <div class="col-md-4 column">
            </div>
            <div class="col-md-4 column">
            </div>
        </div>

</div>

</body>

</html>

<script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
<script type="text/javascript">
    /**
     * 返回input页面
     */
    function back() {
        window.location.href='/daily/input';
    }
</script>