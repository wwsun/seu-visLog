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
    <!-- Page Content -->
    <div id="page-wrapper">
        <div class="container-fluid">
            <div class="page-header">
                <h3>${title}
                    <small>www.made-in-china.com</small>
                </h3>
            </div>

            <div class="row">
                <div class="col-lg-3">
                    <div class="child-panel">
                        <div id="session-total">
                            <h3>Nodes</h3>
                            <h1 id="node-count">0</h1>

                            <h3>Links</h3>
                            <h1 id="link-count">0</h1>
                        </div>
                    </div>
                </div>
                <div id="main" class="col-lg-9"></div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->

            <div class="row">
                <div class="col-lg-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading" id="selected-node-name">Main sources</div>
                        <!--<div class="panel-body child-panel" id="path-detail">-->
                        <div class="panel-body child-panel" id="landing-source">
                        </div>
                    </div>

                </div>
                <div class="col-lg-6">
                    <div class="panel panel-info">
                        <div class="panel-heading">Degree</div>
                        <div class="panel-body child-panel" id="degree-pie">

                        </div>
                    </div>
                    <!-- dataset overview -->
                </div>
            </div>

            <div class="row">
                <div class="col-lg-6">
                    <div class="panel panel-info">
                        <div class="panel-heading">Main referrals of the selected node</div>
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="ref-table">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>URL</th>
                                        <th>Dups</th>
                                    </tr>
                                    </thead>

                                    <tbody id="node-referrals"></tbody>
                                </table>

                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6">
                    <div class="panel panel-info">
                        <div class="panel-heading">Main targets of the selected node</div>
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="req-table">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>URL</th>
                                        <th>Dups</th>
                                    </tr>
                                    </thead>
                                    <tbody id="node-targets"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->

</div>
<#--wrapper-->
<#include "include/footscript.ftl" >

<#--DataTables core lib-->
<script src="js/plugins/dataTables/jquery.dataTables.js"></script>
<script src="js/plugins/dataTables/dataTables.bootstrap.js"></script>

<!-- D3 core javascript -->
<script src="js/d3.js"></script>
<script src="js/pages/link.js"></script>

</body>
</html>