package seu.demo;

import seu.util.WordCount;

import java.io.IOException;

/**
 * Created by Weiwei on 10/19/2014.
 */
public class Demo3 {
    public static void main(String[] args) {
        try {
            WordCount.getInstance().CountWordFromFile("inbound.keywords","inbound.key.js");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
