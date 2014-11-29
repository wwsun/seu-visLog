<!DOCTYPE html>
<html>
<head>
    <title>Home - VisLog</title>
    <meta charset="utf-8">
<#include "include/head.ftl" >
</head>
<body>

<div id="wrapper">
<#include "include/header.ftl" >
    <div id="page-wrapper">
        <div class="container-fluid">
            <div class="page-header">
                <h3> ${title}
                    <small>www.made-in-china.com</small>
                </h3>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default" style="margin-top: 1em">

                        <div id="svg-wrapper" class="panel-body">

                        </div>
                        <!-- /.svg-wrapper -->
                    </div>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->
</div>

<#include "include/footscript.ftl" >
<!-- Force-Layout-Graph -->
<script src="js/vislog/vislog-force.js"></script>
<script src="js/vislog/vislog.js"></script>

</body>
</html>