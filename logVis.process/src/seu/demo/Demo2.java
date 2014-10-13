package seu.demo;


import seu.json.LogToJSONConverter;

/**
 * Created by Weiwei on 10/13/2014.
 */
public class Demo2 {


    public static void main(String[] args) {
        LogToJSONConverter jsonCon = LogToJSONConverter.GetInstance();
        jsonCon.csvToJSON("5th.csv","outsite.json");
    }
}
