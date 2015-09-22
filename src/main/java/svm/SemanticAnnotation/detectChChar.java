package svm.SemanticAnnotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//�ж��ַ����Ƿ��������ַ�
public class detectChChar {
	public static boolean ContainsChchar(String s) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s);
		while (m.find()) {
			return true;
		}
		return false;
	}
}
