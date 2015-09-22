/**
 * Created by user on 2015/7/23.
 */
$(function(){
   // alert("comg");
    //var events = {};
    var event = {};
    var os = '';
    var laodTime = getTime(new Date());
    var url = location.href;
    var brows = '';

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

//alert("I am here");

    for(var v in browser) {
        if (browser[v] != 0) {
            brows = v + browser[v];
            break;
        }
    }
    for (var v in system){
        if(system[v]){
           os = v + system[v];
            break;
        }
    }

    event.os = os;
    event.url = url;
    event.browser = brows;
    event.loadTime = laodTime;
    event.focusTime = "";
    event.text = "";
    //alert("input");
    $(" :input[type='text'],:input[type='email'],:input[type='tel'],textarea[name='comments']").focus(function(){

        event.focusTime = new Date();
    }).change(function(){

    }).blur(function(){
        var $this = $(this);
        event.id = $this.attr("id")? $this.attr("id"):"";
        event.name = $this.attr("name")? $this.attr("name"):"";
        event.text = $this.val()? $this.val():"";
        event.type = "text";
       // alert(event.focusTime);
        event.blurTime = new Date();
        if(event.focusTime != "" && event.blurTime != "")
            event.costTime = "" + ((event.blurTime - event.focusTime))/1000.0;
        else
            event.costTime = "";
        event.blurTime = getTime(event.blurTime);
        event.focusTime = getTime(event.focusTime);
       // alert(event.blurTime);
        sendData();
   //     alert("blur");
    });



    $("select[name='states']").change(function(){
        var $this = $(this);
        event.id = $this.attr("id")? $this.attr("id"):"";
        event.name = $this.attr("name")? $this.attr("name"):"";
        event.type = "select";
        event.text = $this.children("option:selected").text();
        event.focusTime = getTime(new Date());
        event.blurTime = '';
        event.costTime = "";
        sendData();
        //alert($this.children("option:selected").text());
    });

    $(" :checkbox").change(function(){
        var $this = $(this);
        var text = '';
        $(" :checkbox[name='langs']:checked").each(function(i){
            text += $(this).val() + " ";
        });
        event.id = $this.attr("id")? $this.attr("id"):"";
        event.name = $this.attr("name")? $this.attr("name"):"";
        event.focusTime = getTime(new Date());
        event.costTime = "";
        event.blurTime = "";
        event.text = text;
        event.type = "checkbox";
        sendData();
       // alert("chebox");
    });

    $(":radio").change(function(){
        var $this = $(this);
        alert($this.parent().text());
        event.id = $this.attr("id")? $this.attr("id"):"";
        event.name = $this.attr("name")? $this.attr("name"):"";
        event.focusTime = getTime(new Date());
        event.blurTime = "";
        event.costTime = "";
        event.text = $this.parent().text();
        event.type = "radio";
        sendData();

    });

    $("button[type='submit']").click(function(){
        var text = '';
        var $self = $(this);
        $(" :input").each(function(){
            var $this = $(this);
            if($this.val() == "" || !$this.val()){
                var name = $this.attr("name") || $this.attr("id") || $this.attr("class");
                if(name){
                    text += name + ' ';
                }
            }
        });
       // var va = JSON.stringify(text);
        event.id = $self.attr("id")? $self.attr("id"):"";
        event.name = $self.attr("name")? $self.attr("name"):"";
        event.focusTime = getTime(new Date());
        event.blurTime = "";
        event.costTime = '';
        event.text = text;
        event.type = "submit";
        sendData();

    });


    function sendData(){
        var sendmsg = JSON.stringify(event);
        $.ajax({
            url: '/vislog-restful/api/receive/form/store',
            type: 'post',
            data: sendmsg,
            //dataType : 'json',
            contentType: "application/json; charset=utf-8",
            async: false,
            success: function (data) {
                //alert("success");
                //$(".result")[0].innerHTML=data;
            },
            error: function () {
            //    alert("ajax error");
            }
        });
        event = {};
        event.os = os;
        event.url = url;
        event.browser = brows;
        event.loadTime = laodTime;

    }

    function getTime(s){
        var year = s.getFullYear();
        var month = (s.getMonth() + 1) < 10? '0' +(s.getMonth() + 1): (s.getMonth() + 1);
        var day = s.getDate() < 10? '0' + s.getDate(): s.getDate();;
        var hour = s.getHours() < 10? '0' + s.getHours(): s.getHours();
        var minute = s.getMinutes() < 10? '0' + s.getMinutes(): s.getMinutes();
        var second = s.getSeconds()< 10? '0' + s.getSeconds(): s.getSeconds();

        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

});