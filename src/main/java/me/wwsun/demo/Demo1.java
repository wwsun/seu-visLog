package me.wwsun.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Weiwei on 12/22/2014.
 */
public class Demo1 {
    public static void main(String[] args) {
        String str = "lighting.made-in-china.com";

        //1.search engine
        //String regex = "(google|so.360.cn|(sogou|sousou|baidu|bing|yahoo|yandex|ask|search.tb.ask|webcrawler|us.wow|aol).com|go.mail.ru|daum.net)";

        //2.home page
        //String regex = "^(men|tcen|usae).made-in-china.com/?$|www.made-in-china.com/?$|www.made-in-china.com/\\?site_preference=normal$";

        //String regex = "made-in-china.com(/jion/|/logon.do|/human_verify.action|monitor.html|/sign-in)";

        //String regex = "[[\\w]+&&[^(www)]].made-in-china.com";

        //String regex = "^(pt|es|ru|fr|de|jp|kr|nl|sa).made-in-china.com";

        String regex = "^(lighting|tools|construction|furniture|generalmachinery|industrialmachinery|plasticmachinery).made-in-china.com";

        Matcher m = Pattern.compile(regex).matcher(str);
        if(m.find()) {
            System.out.println(m.group());
        } else {
            System.out.println("Not matched!");
        }
    }
}
