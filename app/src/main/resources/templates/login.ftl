<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
    <script type="text/javascript" src="${request.contextPath}/static/brat/lib/jquery.min.js"></script>
    <!-- 引入Jquery_easyui -->
    <script type="text/javascript" src="${request.contextPath}/static/easyui/js/jquery.easyui.min.js" charset="utf-8"></script>
    <!-- 引入easyUi国际化--中文 -->
    <script type="text/javascript" src="${request.contextPath}/static/easyui/js/easyui-lang-zh_CN.js" charset="utf-8"></script>
    <!-- 引入easyUi默认的CSS格式--蓝色 -->
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/easyui/css/easyui.css" />
    <!-- 引入easyUi小图标 -->
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/easyui/css/icon.css" />
    <!-- 引入对应的JS，切记一定要放在Jquery.js和Jquery_Easyui.js后面，因为里面需要调用他们，建议放在最后面 -->
</head>

<body>
<div id="loginWin" class="easyui-window" title="登录" style="width:350px;height:160px;padding:5px;"
     minimizable="false" maximizable="false" resizable="false" collapsible="false">
    <div class="easyui-layout" fit="true">
        <div region="center" border="false" style="padding:5px;background:#fff;border:1px solid #ccc;">
            <form id="loginForm" method="post">
                <div style="padding:5px 0;">
                    <label for="login">帐号:</label>
                    <input type="text" name="accountNo" style="width:260px;"></input>
                </div>
                <div style="padding:5px 0;">
                    <label for="password">密码:</label>
                    <input type="password" name="loginPwd" style="width:260px;"></input>
                </div>
                <div style="padding:5px 0;text-align: center;color: red;" id="showMsg"></div>
            </form>
        </div>
        <div region="south" border="false" style="text-align:center;">
            <button style="padding-left: 10px" iconCls="icon-ok" onclick="login()">登录</button>
            <button iconCls="icon-cancel" onclick="cleardata()">重置</button>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    document.onkeydown = function(e){
        var event = e || window.event;
        var code = event.keyCode || event.which || event.charCode;
        if (code == 13) {
            login();
        }
    }
    $(function(){
        $("input[name='login']").focus();
    });
    function cleardata(){
        $('#loginForm').form('clear');
    }
    function login(){
        if($("input[name='login']").val()=="" || $("input[name='password']").val()==""){
            $("#showMsg").html("用户名或密码为空，请输入");
            $("input[name='login']").focus();
        }else{
            //ajax异步提交
            $.ajax({
                type:"POST",   //post提交方式默认是get
                url:"${request.contextPath}/auth/anonymous/login.do",
                data:$("#loginForm").serialize(),   //序列化
                error:function(request) {      // 设置表单提交出错
                    $("#showMsg").html(request);  //登录错误提示信息
                },
                success:function(data) {
                    document.location = "${request.contextPath}/index.htm";
                }
            });
        }
    }
</script>
</html>