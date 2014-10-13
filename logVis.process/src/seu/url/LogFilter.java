package seu.url;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class LogFilter {
	
	static void getRequestAndReferer(String input, String output){
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output)));

			String line = null;
			String newLine = null;
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				if(items.length!=4){
					System.out.println(items.length);
				} 
				
				String hostName = items[0];
				String request = "http://" + items[2];
				String referer = items[3];
				
				//过滤掉无效的来源标记
				if(!referer.equals("-")){
					newLine = hostName + "\t" + request + "\t" + referer;					
				}
				if(newLine!=null){
					bw.write(newLine);
					bw.newLine();
				}
			}
			System.out.println("Process over: ---statge one---");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void getOutsiteRequests(String input, String output){
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output)));

			String line = null;
			String newLine = null;
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				
				if(items.length!=3){
					System.out.println(items.length);
				} 
				
				String hostName = items[0];
				String request = items[1];
				String referer = items[2];
				
				if(!referer.contains("made-in-china")) {
					newLine = hostName + "\t" + request + "\t" + referer;
				}
					
				if(newLine!=null){
					bw.write(newLine);
					bw.newLine();
				}
			}
			System.out.println("Process over: ---statge Two---");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void cleanOutsiteRequests(String input, String output){
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output)));

			String line = null;
			String newLine = null;
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				
				if(items.length!=3){
					System.out.println(items.length);
				} 
				
				String hostName = items[0];
				String request = items[1];
				String referer = items[2].split("\\?")[0]; //去掉查询参数部分
				
				URL refUrl = null;
				try {
					refUrl = new URL(referer);
				} catch(MalformedURLException e){
					System.out.println("Illegal URL: Pass <" + referer + ">");
				}
				
				if(!referer.contains("made-in-china")&&(refUrl!=null)) {
					newLine = hostName + "\t" + request + "\t" + refUrl.getHost();
				}
					
				if(newLine!=null){
					bw.write(newLine);
					bw.newLine();
				}
			}
			System.out.println("Process over: ---statge Three---");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void getRequestAndRefererHost(String input, String output){
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output)));

			String line = null;
			String newLine = null;
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				
				if(items.length!=3){
					System.out.println(items.length);
				} 
				
				
				String hostName = items[0];
				String refhost = items[2];
				
				
				if(!hostName.equals("-")) {
					String reqhost = hostName+"made-in-china.com";
					newLine = reqhost + "\t" + refhost;
				}
					
				if(newLine!=null){
					bw.write(newLine);
					bw.newLine();
				}
			}
			System.out.println("Process over: ---statge Four---");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    static void getOutsiteHostDistribution(String input, String output){
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    input)));
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(output)));

            String line;
            String newLine = null;
            HashMap<String, Integer> map = new HashMap<String, Integer>();

            while ((line = br.readLine()) != null) {
                String[] items = line.split("\t");

                if(items.length!=3){
                    System.out.println(items.length);
                }


                //String request = items[0];
                String referer = items[1];
                Integer count = Integer.valueOf(items[2]);

                Integer counter = map.get(referer);
                if(counter==null){
                    map.put(referer, count);
                }else{
                    map.put(referer, count+counter);
                }


            }

            Iterator iter = map.keySet().iterator();
            String domain;
            while(iter.hasNext()){
                String key = String.valueOf(iter.next());
                Object val = map.get(key);


                if(key.contains("google")){
                    domain = "www.google.com";
                }else if(key.contains("baidu")){
                    domain = "www.baidu.com";
                }else if(key.contains("bing")){
                    domain = "www.bing.com";
                }else if(key.contains("yahoo")){
                    domain = "search.yahoo.com";
                }else if(key.contains("ask.com")){
                    domain = "www.ask.com";
                }else if(key.contains("doubleclick")){
                    domain = "doubleclick.net";
                }else if(key.contains("amazon")){
                    domain = "www.amazon.com";
                }else if(key.contains("v4soultions")){
                    domain = "www.v4solutions.net";
                }
                else {
                    domain = key;
                }
                newLine = key + "," + val + "," + domain;

                if(newLine!=null){
                    bw.write(newLine);
                    bw.newLine();
                }
            }

            System.out.println("Process over: ---stage Five---");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                bw.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
