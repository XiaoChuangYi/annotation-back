<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/brat/css/style-vis.css"/>

    <script src="${request.contextPath}/static/brat/lib/head.load.min.js"></script>
    <script src="${request.contextPath}/static/brat/lib/jquery.min.js"></script>
    <script src="${request.contextPath}/static/brat/lib/jquery.svg.min.js"></script>
    <script src="${request.contextPath}/static/brat/lib/jquery.svgdom.min.js"></script>
    <script src="${request.contextPath}/static/brat/lib/webfont.js"></script>

    <script src="${request.contextPath}/static/brat/src/util.js"></script>
    <script src="${request.contextPath}/static/brat/src/configuration.js"></script>
    <script src="${request.contextPath}/static/brat/src/dispatcher.js"></script>
    <script src="${request.contextPath}/static/brat/src/visualizer.js"></script>
    <script src="${request.contextPath}/static/brat/src/visualizer_ui.js"></script>
    <script src="${request.contextPath}/static/brat/src/annotation_log.js"></script>
    <script src="${request.contextPath}/static/brat/src/url_monitor.js"></script>

    <!-- 引入Jquery_easyui -->
    <script type="text/javascript" src="${request.contextPath}/static/easyui/js/jquery.easyui.min.js"
            charset="utf-8"></script>
    <!-- 引入easyUi国际化--中文 -->
    <script type="text/javascript" src="${request.contextPath}/static/easyui/js/easyui-lang-zh_CN.js"
            charset="utf-8"></script>
    <!-- 引入easyUi默认的CSS格式--蓝色 -->
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/easyui/css/easyui.css"/>
    <!-- 引入easyUi小图标 -->
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/easyui/css/icon.css"/>
    <!-- 引入对应的JS，切记一定要放在Jquery.js和Jquery_Easyui.js后面，因为里面需要调用他们，建议放在最后面 -->

    <title>Title</title>
</head>
<body class="easyui-layout">

<div data-options="region:'north',split:true" style="height:60px;"></div>
<#--<div data-options="region:'south',title:'South Title',split:true" style="height:100px;"></div>-->
<#--<div data-options="region:'east',title:'工具栏',split:true" style="width:200px;"></div>-->
<div data-options="region:'west',title:'菜单',split:true" style="width:200px;"></div>
<div data-options="region:'center'" style="background:#eee;">
    <div id="tt" class="easyui-tabs" style="width:100%;height:100%;">
        <div title="标注管理" style="display:none;">
            <div class="easyui-layout" data-options="fit:true">
                <div data-options="region:'center'">

                    <table>
                        <th>
                        <td>
                            <div id="brat1">

                            </div>
                        </td>
                        <td>
                            <div style="width: 100px;">
                                <button>JJ</button>
                            </div>
                        </td>
                        </th>
                    </table>

                </div>


            </div>

            <div data-options="region:'east',title:'新词列表',split:true" style="width:300px;">

            </div>

        </div>

    </div>
    <div title="Tab2" data-options="closable:true" style="overflow:auto;display:none;">
        tab2
    </div>
    <div title="Tab3" data-options="iconCls:'icon-reload',closable:true" style="display:none;">
        tab3
    </div>
</div>

</div>
</body>
<script>
    $(function () {

        var webFontURLs = [
            '${request.contextPath}/static/brat/fonts/Astloch-Bold.ttf',
            '${request.contextPath}/static/brat/fonts/PT_Sans-Caption-Web-Regular.ttf',
            '${request.contextPath}/static/brat/fonts/Liberation_Sans-Regular.ttf'
        ];
        $.getJSON("${request.contextPath}/static/json/config.json", function (config) {
            console.log(config);
            $.getJSON("${request.contextPath}/static/json/demo.json", function (data) {
                console.log(data);
                console.log(data.dataList[0]);
                Util.embed('brat1', config, data.dataList[0].bratData, webFontURLs)
            });
        });
    })
    ;
</script>
</html>