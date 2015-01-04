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
                            <h3>Sessions</h3>

                            <h1 id="session-sum">0</h1>

                            <h3>Bounce Rate</h3>
                            <h1 id="bounce-rate">60%</h1>
                        </div>
                    </div>
                </div>
                <!-- /.col-lg-4 -->

                <div class="col-lg-9">
                    <div class="panel panel-default">
                        <div class="panel-heading">各时段会话个数</div>
                        <div class="panel-body child-panel" id="session-timeline">
                        </div>
                    </div>
                </div>

            </div>
            <!-- /.row -->

            <div class="row">
                <div class="col-lg-4">
                    <div class="panel panel-info">
                        <div class="panel-heading">Top Search Engine Traffic</div>
                        <table class="table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Search Engine</th>
                                <th>Contribution</th>
                            </tr>
                            </thead>
                            <tbody id="top-engines"></tbody>
                        </table>
                    </div>
                    <!-- dataset overview -->
                </div>

                <div class="col-lg-8">
                    <div class="panel panel-info">
                        <div class="panel-heading">Top Keywords</div>
                        <div class="panel-body">
                            <canvas id="word_cloud" height="370" width="680"></canvas>
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
<!-- Force-Layout-Graph -->
<!-- Tag Cloud Core JavaScript-->
<script src="js/wordcloud2.js"></script>
<script src="js/main.js"></script>

</body>
</html>