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

<div data-options="region:'north',split:false" style="height:60px;">
<#include "head.ftl">
</div>
<#--<div data-options="region:'south',title:'South Title',split:false" style="height:100px;"></div>-->
<#--<div data-options="region:'east',title:'工具栏',split:false" style="width:200px;"></div>-->
<div data-options="region:'west',title:'菜单',split:false" style="width:200px;">
<#include "menu.ftl">
</div>
<div data-options="region:'center'" style="background:#eee;">
    <div id="tt" class="easyui-tabs" data-options="fit:true">
        <div title="标注管理" style="display:none;">

            <div class="easyui-layout" data-options="fit:true">
                <div data-options="region:'north',split:false" style="height: 100px">

                </div>
                <div data-options="region:'center',title:'数据框'">
                    <table id="dg" style="width:100%;height:50%"
                           data-options="rownumbers:true,singleSelect:true,pagination:true,url:'/static/json/datagrid.json',method:'get'">
                        <thead>
                        <tr>
                            <th data-options="field:'itemid',width:100">Item ID</th>
                            <th data-options="field:'productid',width:100">Product</th>
                            <th data-options="field:'listprice',width:80,align:'right'">List Price</th>
                            <th data-options="field:'unitcost',width:80,align:'right'">Unit Cost</th>
                            <th data-options="field:'attr1',width:240">Attribute</th>
                            <th data-options="field:'status',width:60,align:'center'">Status</th>
                        </tr>
                        </thead>
                    </table>
                    <table class="easyui-datagrid" title="DataGrid Complex Toolbar" style="width:100%;height:50%"
                           data-options="rownumbers:true,singleSelect:true,url:'/static/json/datagrid.json',method:'get',toolbar:'#tb',footer:'#ft'">
                        <thead>
                        <tr>
                            <th data-options="field:'itemid',width:80">Item ID</th>
                            <th data-options="field:'productid',width:100">Product</th>
                            <th data-options="field:'listprice',width:80,align:'right'">List Price</th>
                            <th data-options="field:'unitcost',width:80,align:'right'">Unit Cost</th>
                            <th data-options="field:'attr1',width:240">Attribute</th>
                            <th data-options="field:'status',width:60,align:'center'">Status</th>
                        </tr>
                        </thead>
                    </table>
                    <div id="tb" style="padding:2px 5px;">
                        <div style="padding-bottom: 4px">
                            Date From: <input class="easyui-datebox" style="width:110px">
                            To: <input class="easyui-datebox" style="width:110px">
                            Language:
                            <select class="easyui-combobox" panelHeight="auto" style="width:100px">
                                <option value="java">Java</option>
                                <option value="c">C</option>
                                <option value="basic">Basic</option>
                                <option value="perl">Perl</option>
                                <option value="python">Python</option>
                            </select>
                        </div>
                        <div style="padding-bottom: 4px">
                            Date From: <input class="easyui-datebox" style="width:110px">
                            To: <input class="easyui-datebox" style="width:110px">
                            Language:
                            <select class="easyui-combobox" panelHeight="auto" style="width:100px">
                                <option value="java">Java</option>
                                <option value="c">C</option>
                                <option value="basic">Basic</option>
                                <option value="perl">Perl</option>
                                <option value="python">Python</option>
                            </select>
                        </div>
                        Date From: <input class="easyui-datebox" style="width:110px">
                        To: <input class="easyui-datebox" style="width:110px">
                        Language:
                        <select class="easyui-combobox" panelHeight="auto" style="width:100px">
                            <option value="java">Java</option>
                            <option value="c">C</option>
                            <option value="basic">Basic</option>
                            <option value="perl">Perl</option>
                            <option value="python">Python</option>
                        </select>
                        <a href="#" class="easyui-linkbutton" iconCls="icon-search">Search</a>
                    </div>
                    <div id="ft" style="padding:2px 5px;">
                        <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true"></a>
                        <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true"></a>
                        <a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true"></a>
                        <a href="#" class="easyui-linkbutton" iconCls="icon-cut" plain="true"></a>
                        <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true"></a>
                    </div>
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

        var pager = $('#dg').datagrid().datagrid('getPager');    // get the pager of datagrid
        pager.pagination({
            buttons: [{
                iconCls: 'icon-search',
                handler: function () {
                    alert('search');
                }
            }, {
                iconCls: 'icon-add',
                handler: function () {
                    alert('add');
                }
            }, {
                iconCls: 'icon-edit',
                handler: function () {
                    alert('edit');
                }
            }]
        });

    });
</script>
</html>