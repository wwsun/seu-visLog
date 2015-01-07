<!-- Navigation -->
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/">Server Log Visualization</a>
    </div>
    <!-- /.navbar-header -->

    <ul class="nav navbar-nav">
        <li class="active"><a href="#">Reporting</a></li>
        <li><a href="#">Admin</a></li>
    </ul>
    <!-- /topper-navbar-left -->

    <ul class="nav navbar-top-links navbar-right">
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                <i class="fa fa-tasks fa-fw"></i>
                <i class="fa fa-caret-down"></i>
            </a>
            <ul class="dropdown-menu dropdown-tasks">
                <li>
                    <a href="/update"> Update Overview</a>
                </li>
                <li class="divider"></li>
                <li>
                    <a href="/update/link"> Update Link Analysis</a>
                </li>
                <li class="divider"></li>
                <li>
                    <a href="/update/flow"> Update Flow Analysis</a>
                </li>
                <li class="divider"></li>
                <li>
                    <a class="text-center" href="#">
                        <strong>Update All</strong>
                        <i class="fa fa-angle-right"></i>
                    </a>
                </li>
            </ul>
            <!-- /.dropdown-tasks -->
        </li>
        <!-- /.dropdown -->
    </ul>
    <!-- /.navbar-top-links -->

    <div class="navbar-default sidebar" role="navigation">
        <div class="sidebar-nav navbar-collapse">
            <ul class="nav" id="side-menu">
                <li class="sidebar-search">
                    <div class="input-group custom-search-form">
                        <input type="text" class="form-control" placeholder="Search...">
                                <span class="input-group-btn">
                                    <button class="btn btn-default" type="button">
                                        <i class="fa fa-search"></i>
                                    </button>
                                </span>
                    </div>
                    <!-- /input-group -->
                </li>
                <li>
                    <a href="/" id="overview-tab"><i class="fa fa-dashboard fa-fw"></i> Overview</a>
                </li>

                <li id="link-tab-li">
                    <a href="#" ><i class="fa fa-sitemap fa-fw"></i> Link Analysis<span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="/link" id="link-tab">整站链接分析</a>
                        </li>
                        <li>
                            <a href="#">站外链接分析</a>
                        </li>
                        <li>
                            <a href="#">站内链接分析</a>
                        </li>

                    </ul>
                    <!-- /.nav-second-level -->
                </li>


                <li id="flow-tab-li">
                    <a href="#"><i class="fa fa-bar-chart-o fa-fw"></i> Flow Analysis<span
                            class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="/flow" id="flow-tab">全站流量分析</a>
                        </li>
                        <li>
                            <a href="#">站外流量分析</a>
                        </li>
                        <li>
                            <a href="#">站内流量分析</a>
                        </li>
                    </ul>
                    <!-- /.nav-second-level -->
                </li>

                <li>
                    <a href="#"><i class="fa fa-files-o fa-fw"></i> Key Indexes<span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="blank.html">指标分析</a>
                        </li>
                        <li>
                            <a href="login.html">指标定义</a>
                        </li>
                    </ul>
                    <!-- /.nav-second-level -->
                </li>

                <li>
                    <a href="tables.html"><i class="fa fa-table fa-fw"></i> Data Tables<span
                            class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="panels-wells.html">日志数据</a>
                        </li>
                        <li>
                            <a href="buttons.html">关键字数据</a>
                        </li>
                        <li>
                            <a href="notifications.html">指标数据</a>
                        </li>

                    </ul>
                </li>

                <li>
                    <a href="#"><i class="fa fa-sitemap fa-fw"></i> Reserved<span
                            class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="#">Second Level Item</a>
                        </li>
                        <li>
                            <a href="#">Second Level Item</a>
                        </li>
                        <li>
                            <a href="#">Third Level <span class="fa arrow"></span></a>
                            <ul class="nav nav-third-level">
                                <li>
                                    <a href="#">Third Level Item</a>
                                </li>
                                <li>
                                    <a href="#">Third Level Item</a>
                                </li>
                                <li>
                                    <a href="#">Third Level Item</a>
                                </li>
                                <li>
                                    <a href="#">Third Level Item</a>
                                </li>
                            </ul>
                            <!-- /.nav-third-level -->
                        </li>
                    </ul>
                    <!-- /.nav-second-level -->
                </li>


            </ul>
        </div>
        <!-- /.sidebar-collapse -->
    </div>
    <!-- /.navbar-static-side -->
</nav>