package svm.SemanticAnnotation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;

public class SVMPreprocessor2 {
	private final String separator=" ";
	private void keywordcount(String inputfile, FeatureElement ele) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			int cursor = 0;
			Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
			String line = br.readLine();
			while (line != null && cursor < ele.getkeywords().length) {
				if (cmp.compare(line, ele.getkeywords()[cursor]) < 0) {
					line = br.readLine();
				} else if (cmp.compare(line, ele.getkeywords()[cursor]) == 0) {
					ele.setCount(ele.count() + 1);
	//				System.out.println(ele.count()); 
					line = br.readLine();
				} else if (cmp.compare(line, ele.getkeywords()[cursor]) > 0) {
					cursor++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private int sum_text(String filename) {
		int sum = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
	
					sum++;

			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sum;
	}
	public void featurecount(String inputfolder, String featurefolder,
			String outputfile_svm) {

		File outfile = new File(outputfile_svm);   
        if (outfile.isFile()) {  
        	outfile.delete();  
        }
        
		try {

			File feafolder = new File(featurefolder);
			String[] feafiles = feafolder.list();
			ArrayList<FeatureElement> webfeaeles = new ArrayList<FeatureElement>();
			for (String feafile : feafiles) {
				FeatureElement ele = new FeatureElement(
						feafile.split("\\.")[0]);
				BufferedReader br = new BufferedReader(new FileReader(
						featurefolder + "\\" + feafile));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.equals(""))
						ele.addkeyword(line);
				}
				webfeaeles.add(ele);
				br.close();
			}


			File filefolder = new File(inputfolder);
			String[] files = filefolder.list();


			for (String file : files) {
				for (FeatureElement ele : webfeaeles) {
					ele.setCount(0);
				}
				int text_length = sum_text(inputfolder + "\\" + file);
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile_svm, true));
				int i = 1;
				bw.append("1"+" ");
				for (FeatureElement ele : webfeaeles) {
					keywordcount(inputfolder + "\\" + file, ele);
					bw.append(i + ":" + (float) ele.count() + separator);
					i++;
				}
				bw.newLine();
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
            e.printStackTrace();
        }
	}
}
