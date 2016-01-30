<!DOCTYPE html>
<html lang="zh" ng-app="hiveWebApp">
<head>
    <meta charset="utf-8">
    <meta name="description" content="Hive Web Application">

    <title>data.dp - Hive Web</title>
    <link rel="shortcut icon" href="images/favicon.ico" />

    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="max-age=0">
    <meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate">
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT">

    <base href=".">

    <!-- basic styles -->
    <link rel="stylesheet" href="assets/css/uncompressed/bootstrap.css" />
    <link rel="stylesheet" href="assets/css/font-awesome.min.css" />

    <!-- button styles-->
    <link rel="stylesheet" href="assets/css/buttons.css" />

    <!--[if IE 7]>
        <link rel="stylesheet" href="assets/css/font-awesome-ie7.min.css" />
    <![endif]-->

    <!-- page specific plugin styles -->
    <link rel="stylesheet" href="assets/css/jquery-ui-1.10.3.full.min.css" />
    <link rel="stylesheet" href="assets/css/jquery.gritter.css" />
    <link rel="stylesheet" href="assets/css/bootstrap-editable.css" />
    <link rel="stylesheet" href="assets/css/chosen.css" />
    <link rel="stylesheet" href="assets/css/codemirror/codemirror.css" />
    <link rel="stylesheet" href="assets/css/codemirror/addon/show-hint.css" />
    <link rel="stylesheet" href="assets/css/jquery.dataTables.min.css" />
    <link rel="stylesheet" href="assets/css/dataTables.tableTools.min.css" />
    <!--<link rel="stylesheet" href="assets/css/dataTables.fixedHeader.css" />-->

    <!-- fonts -->
    <link rel="stylesheet" href="assets/css/ace-fonts.css" />

    <!-- ace styles -->
    <link rel="stylesheet" href="assets/css/uncompressed/ace.css" />
    <link rel="stylesheet" href="assets/css/uncompressed/ace-skins.css" />

    <!--[if lte IE 8]>
        <link rel="stylesheet" href="assets/css/ace-ie.min.css" />
    <![endif]-->

    <!-- ace settings handler -->
    <script src="assets/js/uncompressed/ace-extra.js"></script>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->

    <!--[if lt IE 9]>
    <script src="assets/js/html5shiv.js"></script>
    <script src="assets/js/respond.min.js"></script>
    <![endif]-->

    <!-- custom css -->
    <link rel="stylesheet" href="css/common.css" />
    <link rel="stylesheet" href="css/index.css" />
</head>

<body>
    <div class="navbar navbar-default" id="navbar">
        <div class="navbar-container" id="navbar-container">
            <div class="navbar-header pull-left">
                <a href="http://data.dp/" class="navbar-brand"><strong>数据平台</strong></a>
                <a class="navbar-brand"><small>Hive Web</small></a>
                <a class="navbar-brand" href="http://legacy.hive.dp" target="_blank"><small><ins>旧版入口</ins></small></a>
            </div>
            <login-avatar></login-avatar>
        </div>
    </div>

    <div class="main-container" id="main-container">
        <div class="sidebar" id="sidebar">
            <ul class="nav nav-list" sidebar></ul>
            <div class="sidebar-collapse" id="sidebar-collapse">
                <i class="icon-double-angle-left" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
            </div>
        </div>
        <div class="main-content">
            <div class="page-content">
                <div ng-view></div>
            </div>
        </div>
    </div>

    <!-- basic scripts -->
    <!--[if !IE]> -->
    <script type="text/javascript">
        window.jQuery || document.write("<script src='assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
    </script>
    <!-- <![endif]-->
    <!--[if IE]>
    <script type="text/javascript">
        window.jQuery || document.write("<script src='assets/js/jquery-1.10.2.min.js'>"+"<"+"/script>");
    </script>
    <![endif]-->

    <script type="text/javascript">
        if("ontouchend" in document) document.write("<script src='assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
    </script>
    <script src="assets/js/bootstrap.min.js"></script>
    <script src="assets/js/typeahead-bs2.min.js"></script>

    <!-- page specific plugin scripts -->
    <!--[if lte IE 8]>
        <script src="assets/js/excanvas.min.js"></script>
    <![endif]-->
    <script src="assets/js/bootbox.min.js"></script>
    <script src="assets/js/codemirror/codemirror.js"></script>
    <script src="assets/js/codemirror/mode/sql.js"></script>
    <script src="assets/js/codemirror/addon/hint/sql-hint.js"></script>
    <script src="assets/js/codemirror/addon/hint/show-hint.js"></script>
    <script src="assets/js/codemirror/addon/display/placeholder.js"></script>
    <script src="assets/js/fuelux/fuelux.tree.min.js"></script>
    <script src="assets/js/underscore.min.js"></script>
    <script src="assets/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="assets/js/jquery.ui.touch-punch.min.js"></script>
    <script src="assets/js/jquery.slimscroll.min.js"></script>
    <script src="assets/js/jquery.gritter.min.js"></script>
    <script src="assets/js/moment.min.js"></script>
    <script src="assets/js/jquery.dataTables.min.js"></script>
    <!--<script src="assets/js/dataTables.fixedHeader.js"></script>-->
    <script src="assets/js/dataTables.tableTools.min.js"></script>
    <script src="assets/js/chinese-string.js"></script>

    <!-- ace scripts -->
    <script src="assets/js/ace-elements.min.js"></script>
    <script src="assets/js/ace.min.js"></script>

    <!-- angular scripts -->
    <script src="assets/angularjs/angular.js"></script>
    <script src="assets/angularjs/angular-route.js"></script>
    <script src="assets/angularjs/angular-resource.js"></script>

    <!-- angular-ui scripts -->
    <script src="assets/angular_ui_bootstrap/ui-bootstrap-tpls.js"></script>
    <script src="assets/angular_ui_bootstrap/ui-bootstrap-tpls-fix.js"></script>
    <script src="assets/angular_ui_codemirror/ui-codemirror.js"></script>

    <!-- angular file upload -->
    <script src="assets/angular_file_upload/angular-file-upload.min.js"></script>
    <script src="assets/angular_file_upload/console-sham.js"></script>
    <script src="assets/angular_file_upload/es5-sham.min.js"></script>
    <script src="assets/angular_file_upload/es5-shim.min.js"></script>

    <!-- inline scripts related to this page -->
    <script src="js/app.js"></script>
    <script src="js/constants/constants.js"></script>
    <script src="js/services/environment.services.js"></script>
    <script src="js/filters/filters.js"></script>
    <script src="js/services/query.services.js"></script>
    <script src="js/services/keyboardManager.services.js"></script>
    <script src="js/services/filter.services.js"></script>
    <script src="js/controllers/query.controllers.js"></script>
    <script src="js/controllers/query.history.controllers.js"></script>
    <script src="js/controllers/create.table.controllers.js"></script>
    <script src="js/controllers/create.table.controllers.js"></script>
    <script src="js/controllers/feedback.controllers.js"></script>
    <script src="js/services/create.table.services.js"></script>
    <script src="js/directives/directives.js"></script>
    <script src="js/utils/utils.js"></script>
</body>
</html>
