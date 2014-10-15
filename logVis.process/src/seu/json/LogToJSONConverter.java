package seu.json;

/**
 * Created by Weiwei on 10/13/2014.
 */
public class LogToJSONConverter {
    private static final LogToJSONConverter jsonConverter = new LogToJSONConverter();

    private LogToJSONConverter(){

    }

    /**
     * static factory method
     * @return a new instance of LogToJSONConverter
     */
    public static LogToJSONConverter GetInstance(){
        return jsonConverter;
    }


    /**
     *
     * @param input     -   5th.csv
     * @param output    -   outsite.json
     */
    public void csvToJSON(String input, String output){
        //JSONCreater.csvToJSON(input, output);
    }


    /**
     *
     * @param input     -   4th.count.txt
     * @param output    -   outsite2.json
     *                  node: name, group
     *                  link: source, target, value
     */
    public void dataToJSON(String input, String output) {
        JSONCreater.dataToJSON(input, output);
    }
}
