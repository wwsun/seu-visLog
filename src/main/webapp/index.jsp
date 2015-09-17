<html>
<body>
<h2>Hello World!</h2>
<form action="/rest/hellos" method="post">
    <input type="text" name="name"/>
    <input type="button" class="button1"/>接收数据
    <input type="button" class="button2"/>2没有用
    <input type="button" class="button3"/>选择session
</form>
<div class="result">aaaa</div>
</body>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs
/jquery/1.4.0/jquery.min.js"></script>
<script>
    $(document).ready(function(){
        $(".button1").click(function(){
            var jsondata='{"url": "www.baidu.com","time": "2014-10-20 15:30","events": [{"element": "ele1","semantics": "Parameters","event": "scroll"},{"element": "ele1","semantics": "Pictures","event": "click"},{"element": "ele2","semantics": "Comments","event": "mousemove"}]}';
            var htmlobj=$.ajax({
                url : '/vislog-restful/api/receive/events/store',
                type : 'post',
                data : jsondata,
                //dataType : 'json',
                contentType: "application/json; charset=utf-8",
                async : false,
                success : function(data) {
                    alert("success");
                    $(".result")[0].innerHTML=data;
                },
                error : function() {
                    alert("ajax error");
                }
            });

            console.log(htmlobj);
        });
    });

    $(document).ready(function(){
        $(".button2").click(function(){
            var htmlobj=$.ajax({
                url : '/vislog-restful/api/receive/events/test',
                type : 'get',
                async : false,
                success : function(data) {
                    alert("success");
                    $("div.result")[0].innerHTML=data;
                },
                error : function() {
                    alert("ajax error");
                }
            });

            console.log(htmlobj);
        });
    });
//
 //用来筛选session，前端应该给出一个筛选器
    $(document).ready(function(){
        $(".button3").click(function(){
            var jsondata='{"category": 2552 ,"date": "2014-08-10","last":300,"pages":10,"country":"United States"}';
            var htmlobj=$.ajax({
                url : '/vislog-restful/api/visitflow/filter',
                type : 'post',
                data : jsondata,
                //dataType : 'json',
                contentType: "application/json; charset=utf-8",
                async : false,
                success : function(data) {
                    alert("success");
                    $(".result")[0].innerHTML=data;
                },
                error : function() {
                    alert("ajax error");
                }
            });

            console.log(htmlobj);
        });
    });

</script>
</html>
