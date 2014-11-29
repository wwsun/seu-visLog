<!DOCTYPE html>
<html>
<head>
    <title>Home - VisLog</title>
    <meta charset="utf-8">
    <#include "include/head.ftl" >
    <link rel="stylesheet" href="css/plugins/dataTables.bootstrap.css">
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
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Inbound website distribution
                        </div>
                        <div class="panel-body">
                            <table id="inbound-table" class="table table-striped table-bordered table-hover" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th>name</th>
                                    <th>size</th>
                                    <th>group</th>

                                </tr>
                                </thead>

                                <tfoot>
                                <tr>
                                    <th>name</th>
                                    <th>size</th>
                                    <th>group</th>

                                </tr>
                                </tfoot>
                            </table>
                        </div>

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

<#include "include/foottable.ftl" >
<script src="js/vislog/vislog-table.js"></script>

</body>
</html>