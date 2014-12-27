package me.wwsun.util;

import com.mongodb.util.JSON;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Weiwei on 12/27/2014.
 */
public class FileUtil {

    public static void outputAsJSON(Object object, String fileName) {
        Path pagePath = null;
        try {
            pagePath = Paths.get("./target/classes/public/data/"+fileName+".json").toRealPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(pagePath,
                StandardCharsets.UTF_8)) {
            writer.write(JSON.serialize(object));
            System.out.println("Successfully output to the target file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
