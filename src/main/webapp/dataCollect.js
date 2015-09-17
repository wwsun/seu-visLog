/**
 * Created by user on 2015/7/10.
 */
(function(window, document){

    var pageInfo = {
        getCodeName:function(){
            return navigator.appCodeName;
        },
        getUserAgent:function(){
            return navigator.userAgent;
        },
        getPlatform:function(){
            return navigator.platform;
        },
        /*  getBrowerLanguage:function(){
         return navigator.browerLanguage;
         },
         */

        /* getCpuClass:function(){
         return navigator.cpu
         }*/
        getAppName:function(){
            return navigator.appName;
        },
        getAppVersion:function(){
            return navigator.appVersion;
        },
        getReferrer:function(){
            return document.referrer;
        },
        getUrl:function(){
            return location.href;
        }
    };

    //rendering engines
    var engine = {
        ie: 0,
        gecko: 0,
        webkit: 0,
        khtml: 0,
        opera: 0,
        //complete version
        ver: null
    };
    //browsers
    var browser = {
        //browsers
        ie: 0,
        firefox: 0,
        safari: 0,
        konq: 0,
        opera: 0,
        chrome: 0,
        //specific version
        ver: null
    };
    //platform/device/OS
    var system = {
        win: false,
        mac: false,
        x11: false,
        //mobile devices
        iphone: false,
        ipod: false,
        ipad: false,
        ios: false,
        android: false,
        nokiaN: false,
        winMobile: false,
        //game systems
        wii: false,
        ps: false
    };


    var clientSEU101010827 = function(){
        //detect rendering engines/browsers
        var ua = navigator.userAgent;
        if (window.opera){
            engine.ver = browser.ver = window.opera.version();
            engine.opera = browser.opera = parseFloat(engine.ver);
        } else if (/AppleWebKit\/(\S+)/.test(ua)){
            engine.ver = RegExp["$1"];
            engine.webkit = parseFloat(engine.ver);
            //figure out if it��s Chrome or Safari
            if (/Chrome\/(\S+)/.test(ua)){
                browser.ver = RegExp["$1"];
                browser.chrome = parseFloat(browser.ver);
            } else if (/Version\/(\S+)/.test(ua)){
                browser.ver = RegExp["$1"];
                browser.safari = parseFloat(browser.ver);
            } else {
                //approximate version
                var safariVersion = 1;
                if (engine.webkit < 100){
                    safariVersion = 1;
                } else if (engine.webkit < 312){
                    safariVersion = 1.2;
                } else if (engine.webkit < 412){
                    safariVersion = 1.3;
                } else {
                    safariVersion = 2;
                }
                browser.safari = browser.ver = safariVersion;
            }
        } else if (/KHTML\/(\S+)/.test(ua) || /Konqueror\/([^;]+)/.test(ua)){
            engine.ver = browser.ver = RegExp["$1"];
            engine.khtml = browser.konq = parseFloat(engine.ver);
        } else if (/rv:([^\)]+)\) Gecko\/\d{8}/.test(ua)){
            engine.ver = RegExp["$1"];
            engine.gecko = parseFloat(engine.ver);
            //determine if it��s Firefox
            if (/Firefox\/(\S+)/.test(ua)){
                browser.ver = RegExp["$1"];
                browser.firefox = parseFloat(browser.ver);
            }
        } else if (/MSIE ([^;]+)/.test(ua)){
            engine.ver = browser.ver = RegExp["$1"];
            engine.ie = browser.ie = parseFloat(engine.ver);
        }
        //detect browsers
        browser.ie = engine.ie;
        browser.opera = engine.opera;
        //detect platform
        var p = navigator.platform;
        system.win = p.indexOf("Win") == 0;
        system.mac = p.indexOf("Mac") == 0;
        system.x11 = (p == "X11") || (p.indexOf("Linux") == 0);
        //detect windows operating systems
        if (system.win){
            if (/Win(?:dows )?([^do]{2})\s?(\d+\.\d+)?/.test(ua)){
                if (RegExp["$1"] == "NT"){
                    switch(RegExp["$2"]){
                        case "5.0":
                            system.win = "2000";
                            break;
                        case "5.1":
                            system.win = "XP";
                            break;
                        case "6.0":
                            system.win = "Vista";
                            break;
                        case "6.1":
                            system.win = "7";
                            break;
                        default:
                            system.win = "NT";
                            break;
                    }
                } else if (RegExp["$1"] == "9x"){
                    system.win = "ME";
                } else {
                    system.win = RegExp["$1"];
                }
            }
        }
        //mobile devices
        system.iphone = ua.indexOf("iPhone") > -1;
        system.ipod = ua.indexOf("iPod") > -1;
        system.ipad = ua.indexOf("iPad") > -1;
        system.nokiaN = ua.indexOf("NokiaN") > -1;
        //windows mobile
        if (system.win == "CE"){
            system.winMobile = system.win;
        } else if (system.win == "Ph"){
            if(/Windows Phone OS (\d+.\d+)/.test(ua)){;
                system.win = "Phone";
                system.winMobile = parseFloat(RegExp["$1"]);
            }
        }
        //determine iOS version
        if (system.mac && ua.indexOf("Mobile") > -1){
            if (/CPU (?:iPhone )?OS (\d+_\d+)/.test(ua)){
                system.ios = parseFloat(RegExp.$1.replace("_", "."));
            } else {
                system.ios = 2; //can��t really detect - so guess
            }
        }
        //determine Android version
        if (/Android (\d+\.\d+)/.test(ua)){
            system.android = parseFloat(RegExp.$1);
        }
        //gaming systems
        system.wii = ua.indexOf("Wii") > -1;
        system.ps = /playstation/i.test(ua);
        //return it
        return {
            engine: engine,
            browser: browser,
            system: system
        };
    }();

    // alert(engine.ver);

    var sendData = {};

    var browerInfo = '';

   /* for (var v in engine){
        if(engine[v] != 0){
            browerInfo += v + ":" + engine[v];
          //  sendData[v] = engine[v];
          //  sendData.engine = v + engine[v];
            break;
        }
    }
    */
    for(var v in browser) {
        if (browser[v] != 0) {
            browerInfo += ',' + v + ":" + browser[v];
            //sendData[v] = browser[v];
            sendData.browser = v + browser[v];
            break;
        }
    }
    for (var v in system){
        if(system[v]){
            browerInfo += ',' +  v + ":" + system[v];
            //sendData[v] = system[v];
            sendData.os = v + system[v];
            break;
        }
    }



    //alert(browerInfo);

    /*  var iHeader=["time:"+new Date()
     ,"url:"+location.href
     ,"refer:"+document.referrer
     ,"browser:"+navigator.appCodeName];
     //collected data
     var iData=[];
     */

    function getTime(){
        var s = new Date();
        var year = s.getFullYear();
        var month = (s.getMonth() + 1) < 10? '0' +(s.getMonth() + 1): (s.getMonth() + 1);
        var day = s.getDate() < 10? '0' + s.getDate(): s.getDate();;
        var hour = s.getHours() < 10? '0' + s.getHours(): s.getHours();
        var minute = s.getMinutes() < 10? '0' + s.getMinutes(): s.getMinutes();
        var second = s.getSeconds()< 10? '0' + s.getSeconds(): s.getSeconds();

        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }
    sendData.url = location.href;
    sendData.loadtime = getTime();
    sendData.refer = document.referrer;
    //sendData.browser = navigator.appCodeName;





    function GetOffset (object, offset) {
        if (!object)
            return;
        offset.x += object.offsetLeft;
        offset.y += object.offsetTop;

        GetOffset (object.offsetParent, offset);
    }

    function GetScrolled (object, scrolled) {
        if (!object)
            return;
        scrolled.x += object.scrollLeft;
        scrolled.y += object.scrollTop;

        if (object.tagName.toLowerCase () != "html") {
            GetScrolled (object.parentNode, scrolled);
        }
    }

    // always return 1, except at non-default zoom levels in IE before version 8
    function GetZoomFactor () {
        var factor = 1;
        if (document.body.getBoundingClientRect) {
            // rect is only in physical pixel size in IE before version 8
            var rect = document.body.getBoundingClientRect ();
            var physicalW = rect.right - rect.left;
            var logicalW = document.body.offsetWidth;

            // the zoom level is always an integer percent value
            factor = Math.round ((physicalW / logicalW) * 100) / 100;
        }
        return factor;
    }

    function GetBox (obj) {
        //var div = document.getElementById ("myDiv");
        var x = 0;
        var y = 0;
        var w = 0;
        var h = 0;

        if (obj.getBoundingClientRect) {        // Internet Explorer, Firefox 3+, Google Chrome, Opera 9.5+, Safari 4+
            var rect = obj.getBoundingClientRect ();
            x = rect.left;
            y = rect.top;
            w = rect.right - rect.left;
            h = rect.bottom - rect.top;

            if (navigator.appName.toLowerCase () == "microsoft internet explorer") {
                // the bounding rectangle include the top and left borders of the client area
                x -= document.documentElement.clientLeft;
                y -= document.documentElement.clientTop;

                var zoomFactor = GetZoomFactor ();
                if (zoomFactor != 1) {  // IE 7 at non-default zoom level
                    x = Math.round (x / zoomFactor);
                    y = Math.round (y / zoomFactor);
                    w = Math.round (w / zoomFactor);
                    h = Math.round (h / zoomFactor);
                }
            }
        }
        else {
            // older Firefox, Opera and Safari versions
            var offset = {x : 0, y : 0};
            GetOffset (obj, offset);

            var scrolled = {x : 0, y : 0};
            GetScrolled (obj.parentNode, scrolled);

            x = offset.x - scrolled.x;
            y = offset.y - scrolled.y;
            w = obj.offsetWidth;
            h = obj.offsetHeight;
        }



        //alert ("Left: " + x + "\nTop: " + y + "\nWidth: " + w + "\nHeight: " + h);
        return {
            Left:x,
            Top:y,
            Width:w,
            Height:h
        }
    }

    function search(node,dh,dw) {

        //alert("search");
        var chlidNodes = node.childNodes;

        for (var i = 0; i < chlidNodes.length; i++) {

            //alert(chlidNodes[i].firstChild.nodeType);
            //alert(chlidNodes[i].nodeName);
            if (chlidNodes[i].tagName == "DIV") continue;
            if ((chlidNodes[i].nodeType == 3)) {
                // alert(chlidNodes[i].tagName + ":" + chlidNodes[i].nodeValue);
                if (chlidNodes[i].nodeValue.trim() != "") {
                    scrolldata += chlidNodes[i].nodeValue.trim();
                    // alert(s);
                }
            } else {
                var local = GetBox(chlidNodes[i]);
                //alert(chlidNodes[i].firstChild.nodeType);
                if (local.Left > 0 && local.Top > 0 && local.Left < dw && local.Top < dh)
                    search(chlidNodes[i],dh,dw);
            }


        }

    }





    var events = [];
    var scrolldata = '';
    var lastvalue = '';
    var lasteventtype='';
    //register event handler
    window.iWatchHandler=function (event){
        //alert("heh");
       // alert(event.type);
        var e = {};
        scrolldata = '';
        // iData.push(browerInfo);
        event = event || window.event;
        var s = event.target || event.srcElement;// document.elementFromPoint(x,y);
        /*  var par=["time:","element:","event:","location:","value:"];
         var data=par[0]+new Date()+','
         +par[1]+ s +"|id="+s.id+','
         +par[2]+event.type+','
         +par[3]+event.clientX+"|"+event.clientY;
         */
        //alert("good");
        e.time = getTime();
        e.element = s.tagName? s.tagName:'';
        e.id = s.id? s.id:"";
        e.event = event.type;

        var x = 0;
        var y = 0;
        // alert('pageX' + evt.pageX);
        //  alert('clientX' + evt.clientX);
        if(event.pageX) { //���Ϊfirefox chrome safari�������
            x = event.pageX;
            y = event.pageY;

        }else if(event.clientX) { //IE
            var offsetX = 0;
            var offsetY = 0;
            //���ΪIE6���ϵİ汾
            if (document.documentElement.scrollLeft) {
                offsetX = document.documentElement.scrollLeft;
                offsetY = document.documentElement.scrollTop;
            } else if (document.body.scrollLeft) { //���ɵ�ie�汾��
                offsetX = document.body.scrollLeft;
                offsetY = document.body.scrollTop;
            }
            x = event.clientX + offsetX;
            y = event.clientY + offsetY;

        }
        //alert(x + ',' + y);
       // alert("heihei");
       // alert(event.type == 'scroll');
        var value = '';

        if(event.type == "scroll"){
            //alert("scroll");
            var divs = document.getElementsByTagName("div");
            //alert(divs.length);
            var dw = document.documentElement.clientWidth//document.body.clientWidth;
            var dh = document.documentElement.clientHeight;
            for (var i = 0; i < divs.length; i++) {
                var v = divs.item(i);
                //alert(v.tagName);
                var location = GetBox(v);
                //alert(GetBox(v).Top);
                if(location.Left > 0 && location.Top > 0 && location.Left < dw && location.Top < dh)
                // var s = v.getBoundingClientRect();
                // if (s.top > 0 && s.left > 0)
                    search(v,dh,dw);
            }
           // alert(scrolldata);
            e.top = '' +  (document.documentElement.scrollTop || document.body.scrollTop);
            e.left ='' + document.documentElement.scrollLeft || document.body.scrollLeft;
            e.height ='' +  document.documentElement.scrollHeight || document.body.scrollHeight;
            e.width = '' + document.documentElement.scrollWidth || document.body.scrollWidth;
           value = scrolldata;
        }
        else {
            var local = GetBox(s);
            e.left = '' + local.Left;
            e.top = '' + local.Top;
            e.height = '' + local.Height;
            e.width = '' + local.Width;

            if (s.tagName == 'IMG') {
                    value = s.alt;

            } else {
                if (s.firstChild.nodeValue == '') {
                    //    data = data + ',' + par[4] + '-';
                    value = ''
                }
                else {
                    // data = data + ',' + par[4] + s.firstChild.nodeValue;
                    value = s.firstChild.nodeValue.trim();
                }
            }
        }


            if (lastvalue != value) {
                e.text = value;
                events.push(e);
                lastvalue = value;
                lasteventtype = event.type
                // data += ',Left:' + local.Left + ',Top:' + local.Top + ',Width:' + local.Width
                //         + ',Height:' + local.Height;
            } else {
                if(lasteventtype != event.type){
                    e.text = value;
                    events.push(e);
                }
                lastvalue = value;
                lasteventtype = event.type;
            }


        // iData.push(data);
        // alert(iData);

    };



   document.onclick = window.iWatchHandler;
    document.onmousemove = window.iWatchHandler;
   document.onscroll =  window.iWatchHandler;
    //specified event types
    var eventTypes=["\"click\"", "\"mouseover\"","\"scroll\""], eventTypeEx=[];

    //attach event handler to specified event types
    /*  eventTypes.forEach(function(item,index, arry){
     document.write("<script type=\"text/javascript\">"
     +"document.body.addEventListener("
     +item
     +","+" iWatchHandler,true)"+"<\/script>");}
     );

     */

    function getRand(){

        var randId = new Date().getTime() + '-';
        for (var i = 0 ;  i < 32; i++){
            randId += Math.floor(Math.random() *10);
        }

        return randId;
    }






    setInterval(function(){
        //prefix header for sending
        //var forSending=iHeader.concat(iData.splice(0,5));
        sendData.events = events;

       // sendData.id = getRand();
       // alert(JSON.stringify(sendData));
        var datas = JSON.stringify(sendData);
       // var jsondata='{"url": "www.baidu.com","sex":"male",time": "2014-10-20 15:30","events": [{"element": "ele1","semantics": "Parameters","event": "scroll"},{"element": "ele1","semantics": "Pictures","event": "click"},{"element": "ele2","semantics": "Comments","event": "mousemove"}]}';

        if(events.length > 0) {
            $.ajax({
                url: '/vislog-restful/api/receive/events/store',
                type: 'post',
                data: datas,
                //dataType : 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function (data) {
                    alert("success");
                    //$(".result")[0].innerHTML=data;
                },
                error: function () {
                    alert("ajax error");
                }
            });
        }
       // console.log(htmlobj);

        events = [];
        //alert(getRand() + ',' + forSending.toString());
        //alert('interval');
        //sending


    }, 10000)

})(window, document);