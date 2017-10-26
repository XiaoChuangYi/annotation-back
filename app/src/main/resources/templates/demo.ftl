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

    <title>Title</title>
</head>
<body>


<div id="brat1">

</div>
<div id="brat2">

</div>
<script>
    $(function () {

        var webFontURLs = [
            '${request.contextPath}/static/brat/fonts/Astloch-Bold.ttf',
            '${request.contextPath}/static/brat/fonts/PT_Sans-Caption-Web-Regular.ttf',
            '${request.contextPath}/static/brat/fonts/Liberation_Sans-Regular.ttf'
        ];
        alert("你好");
        $.getJSON("${request.contextPath}/static/json/config.json", function (config) {
            console.log(config);
            $.getJSON("${request.contextPath}/static/json/demo.json", function (data) {
                console.log(data);
                console.log(data.dataList[0]);
                Util.embed('brat1', config, data.dataList[0].bratData, webFontURLs)
                Util.embed('brat2', config, data.dataList[0].bratData, webFontURLs)

//                Util.embed('brat', config, data.dataList[0], webFontURLs)
            });
        });

    <#--$.ajax({-->
    <#--type: "POST",-->
    <#--url: '${request.contextPath}/static/json/config.json',-->
    <#--success: function (data) {-->
    <#--console.log(data);-->
    <#--}-->
    <#--})-->
    })
    ;
</script>

</body>
</html>