package svm.SemanticAnnotation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import svm.service.svm_predict;

public class Predict {

    public static void sortsequence(String inputfolder, String outputfolder) {
        try {
            File filefolder = new File(inputfolder);
            String[] files = filefolder.list();
            for (String file : files) {
                BufferedReader br = new BufferedReader(new FileReader(
                        inputfolder + "\\" + file));
                ArrayList<String> wordsarray = new ArrayList<String>();
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.equals(""))
                        wordsarray.add(line);
                }
                String[] words = new String[wordsarray.size()];
                wordsarray.toArray(words);
                Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
                Arrays.sort(words, cmp);


                File outFile = new File(outputfolder + "\\" + file);

                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        outFile));
                for (String word : words) {
                    bw.write(word);
                    bw.newLine();
                }
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 具体标注函数
    public static String SemanticPredict(String s) throws IOException {
        //模型相关
        String modelFile = "svmdata\\last.model";    //训练好的模型文件
        String featureFolder = "svmdata\\features";    //特征文件目录
        String result = "";
        //临时目录&文件
        String parseFolder = "svmdata\\parse";
        String parseSortedFolder = "svmdata\\parseSortedFolder";
        String featureResultFile = "svmdata\\feature_result";  //输入文本的特征结果文件
        String predictResult = "svmdata\\predict_result";  //预测结果文件

        //分词
        List<Term> parse = ToAnalysis.parse(s);
        //创建分词输出目录和文件
        File dir = new File(parseFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File parseOutFile = new File(parseFolder + "\\parse.txt");
        parseOutFile.createNewFile();
        //打开输出流
        BufferedWriter bw = new BufferedWriter(new FileWriter(parseOutFile));
        for (Term t : parse) {
            if (t.toString().contains("/")
                    && detectChChar.ContainsChchar(t.toString())) {
                String tsub = t.toString().substring(0,
                        t.toString().indexOf("/"));
                if (tsub.trim().length() >= 1) {

                    bw.append(tsub.trim());
                    bw.newLine();
                }
            } else if (t.toString().contains("￥")) {
                bw.append("￥");
                bw.newLine();
            }
        }
        bw.flush();
        bw.close();
        //分词结果写入完毕
        //分词结果排序
        sortsequence(parseFolder, parseSortedFolder);
        //计算特征向量
        SVMPreprocessor2 preprocessor = new SVMPreprocessor2();
        preprocessor.featurecount(parseSortedFolder, featureFolder,
                featureResultFile);

        String[] argvPredict = {featureResultFile, // 特征结果文件
                modelFile, // 训练好的模型文件
                predictResult // 预测结果文件
        };

        svm_predict svmp = new svm_predict();
        svmp.main(argvPredict);

        BufferedReader br = new BufferedReader(new FileReader(predictResult));
        String line = br.readLine();
        switch (line) {
            case "1.0":
                result = "评价";
                break;
            case "2.0":
                result = "服务";
                break;
            case "3.0":
                result = "价格";
                break;
            case "4.0":
                result = "商品描述";
                break;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String result1 = SemanticPredict("远程服务售后政策和保障范围：远程服务不可退货。");
        System.out.println(result1);
        String result2 = SemanticPredict("远程服务售后政策和保障范围");
        System.out.println(result2);
        String result3 = SemanticPredict("发货速度真快啊！手机不错，样子好看，屏幕清晰，该送的都送了，没\n" +
                "看吧！客服唐嫣全5分服务，真是值得一买！就是不知道电池耐不耐，开机的话时间有点长，\n" +
                "来吧！先用用过之后再来评价吧\",");
        System.out.println(result3);
    }
}














