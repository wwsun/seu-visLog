package svm.SemanticAnnotation;

import java.util.ArrayList;

public class FeatureElement {
	private double weight=0;
	private int count=0;
	private String name=null;
	private ArrayList<String> keywords=new ArrayList<String>();
	
	FeatureElement(String name){
		this.name=name;
	}

	public String getEleName() {
		return this.name;
	}


	public void setFeaWeight(double w) {
		this.weight=w;
	}


	public double getFeaWeight() {
		return weight;
	}


	public int count() {
		return count;
	}


	public void setCount(int c) {
		this.count=c;
	}


	public void addkeyword(String s) {
		keywords.add(s);
	}


	public String[] getkeywords() {
		String[] words=new String[this.keywords.size()];
		this.keywords.toArray(words);
		return words;
	}

}
