package cn.edu.sysu.searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.edu.sysu.util;
import cn.edu.sysu.config.ConfigOperation;
import cn.edu.sysu.syntaxsimilar.Compare;

public class FragmentHashSearchEngine {
	
	public static void main(String[] args){
		int i = 0;
		String rootPath = ConfigOperation.getConfigProperties("basePath6");	
		Map<Integer,Double> similarList = new TreeMap<Integer,Double>();
		for (int ver = 1; ver < 24345; ver++){
			String basePath1 = rootPath + "/" + ver + "/new/src/";
			String basePath2 = rootPath + "/" + ver + "/old/src/";
			
			File f1 = new File(basePath1);
			File f2 = new File(basePath2);
			if(!f1.exists() || !f2.exists()){
				continue;
			}

			if ((util.countJavaFiles(basePath1) == 1) && (util.countJavaFiles(basePath2) == 1)){
				if(util.isFileExisting(rootPath,ver)){
					//double similar = compareSyntaxSimilar4Fragments("D:/log/546/fragmenthash/fragmenthash.txt","D:/log/",ver);
					//double similar = compareSyntaxSimilar4Fragments("D:/log/390/fragmenthash/fragmenthash.txt","D:/log/",ver);
					//double similar = compareSyntaxSimilar4Fragments("D:/log/407/fragmenthash/fragmenthash.txt","D:/log/",ver);
					//double similar = compareSyntaxSimilar4Fragments("D:/log/493/fragmenthash/fragmenthash.txt","D:/log/",ver);
					//double similar = compareSyntaxSimilar4Fragments("D:/data/jedit/546/fragmenthash/fragmenthash.txt",rootPath,ver);
					double similar = compareSyntaxSimilar4Fragments("D:/data/hsqldb/221/fragmenthash/fragmenthash.txt",rootPath,ver);
					//double similar = compareSyntaxSimilar4Fragments("D:/data/jhotdraw/133/fragmenthash/fragmenthash.txt",rootPath,ver);
					if(similar == -1){//source path is not existing
						break;
					}
					similarList.put(ver, similar);
					System.out.println(ver+":"+similar);
				}else{
					continue;
				}
				
			}
		}
		System.out.println("Total number of code fragments:"+similarList.size());		
		output(similarList,rootPath);


	}
	
	public static void outputSimilar(String resultPath, TreeMap<String, String> similarList,String sourceBasePath, int sourceVersion) {
		ArrayList<String> keyValue = util.sortSearchResult2(similarList);
		int j = 0;
		ArrayList<String> pathVersion = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		if (keyValue.size() >= 10) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < 10) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}
            String sourceCommitInfor = util.readVerInfoFile(sourceBasePath, sourceVersion);
            util.writeSearchResult(sourceBasePath,sourceCommitInfor,resultPath, sourceVersion);
			for (int i = 0; i < 10; i++) {
				String[] sim = value.get(i).split("&");
				String[] pv = pathVersion.get(i).split("&");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				String out = null;
				if (sim.length > 3) {
					String info = "rootPath=" + rootPath + "(" + version + ")==================similarity=" + sim[0]
							+ "===================syntaxSimilarity=" + sim[1] + "===================semanticSimilarity="
							+ sim[2] + "===================syntaxSimilar4OriginalCode=" + sim[3];
					System.out.println(info);
					util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
					out = util.readVerInfoFile(rootPath, version);
				} else {
					String info = "rootPath=" + rootPath + "(" + version + ")==================similarity=" + sim[0]
							+ "===================syntaxSimilarity=" + sim[1] + "===================semanticSimilarity="
							+ sim[2] + "===================";
					System.out.println(info);
					util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
					out = util.readVerInfoFile(rootPath, version);
				}

				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
					util.writeSearchResult(sourceBasePath,out,resultPath, sourceVersion);
				}
			}
			String split = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
			util.writeSearchResult(sourceBasePath,split,resultPath, sourceVersion);
		} else if (keyValue.size() > 0 && keyValue.size() < 10) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < keyValue.size()) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}

			 String sourceCommitInfor = util.readVerInfoFile(sourceBasePath, sourceVersion);
	         util.writeSearchResult(sourceBasePath,sourceCommitInfor,resultPath, sourceVersion);
			for (int i = 0; i < keyValue.size(); i++) {
				String[] sim = value.get(i).split("&");
				String[] pv = pathVersion.get(i).split("&");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				String out = null;
				if (sim.length > 3) {
					String info = "rootPath=" + rootPath + "(" + version + ")==================similarity=" + sim[0]
							+ "===================syntaxSimilarity=" + sim[1] + "===================semanticSimilarity="
							+ sim[2] + "===================syntaxSimilar4OriginalCode=" + sim[3];
					System.out.println(info);
					util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
					out = util.readVerInfoFile(rootPath, version);
				} else {
					String info = "rootPath=" + rootPath + "(" + version + ")==================similarity=" + sim[0]
							+ "===================syntaxSimilarity=" + sim[1] + "===================semanticSimilarity="
							+ sim[2] + "===================";
					System.out.println(info);
					util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
					out = util.readVerInfoFile(rootPath, version);
				}
				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
					util.writeSearchResult(sourceBasePath,out,resultPath, sourceVersion);
				}
			}

			String split = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
			util.writeSearchResult(sourceBasePath,split,resultPath, sourceVersion);
		} else {
			System.out.println("null");
		}
	}
	
	public static void outputSimilar2(String resultPath, TreeMap<String, String> similarList, String sourceBasePath, int sourceVersion) {
		ArrayList<String> keyValue = util.sortSearchResult2(similarList);
		int j = 0;
		ArrayList<String> pathVersion = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		if (keyValue.size() >= 10) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < 10) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}
            String sourceCommitInfor = util.readVerInfoFile(sourceBasePath, sourceVersion);
            util.writeSearchResult(sourceBasePath,sourceCommitInfor,resultPath, sourceVersion);
			for (int i = 0; i < 10; i++) {
				String sim = value.get(i);
				//pathVersion.get(i).replaceAll(System.getProperty("file.separator")+"\\", "/");;
				String[] pv = pathVersion.get(i).split("/");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				String out = null;
				
					String info = "rootPath=" + rootPath + "(" + version + ")==================syntaxSimilarity=" + sim
							+ "====================" ;
					System.out.println(info);
					util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
					out = util.readVerInfoFile(rootPath, version);	
				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
					util.writeSearchResult(sourceBasePath,out,resultPath, sourceVersion);
				}
			}
			String split = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
			util.writeSearchResult(sourceBasePath,split,resultPath, sourceVersion);
		} else if (keyValue.size() > 0 && keyValue.size() < 10) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < keyValue.size()) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}

			 String sourceCommitInfor = util.readVerInfoFile(sourceBasePath, sourceVersion);
	         util.writeSearchResult(sourceBasePath,sourceCommitInfor,resultPath, sourceVersion);
			for (int i = 0; i < keyValue.size(); i++) {
				String sim = value.get(i);
				String[] pv = pathVersion.get(i).split("/");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				String out = null;
				String info = "rootPath=" + rootPath + "(" + version + ")==================syntaxSimilarity=" + sim
						+ "====================" ;
				System.out.println(info);
				util.writeSearchResult(sourceBasePath, info, resultPath, sourceVersion);
				out = util.readVerInfoFile(rootPath, version);	
				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
					util.writeSearchResult(sourceBasePath,out,resultPath, sourceVersion);
				}
			}

			String split = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
			util.writeSearchResult(sourceBasePath,split,resultPath, sourceVersion);
		} else {
			System.out.println("null");
		}
	}
	
	public static void output(TreeMap<String, Double> similarList) {
		ArrayList<String> keyValue = util.sortSearchResult1(similarList);
		int j = 0;
		ArrayList<String> pathVersion = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		if (keyValue.size() >= 10) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < 10) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}

			for (int i = 0; i < 10; i++) {
				String sim = value.get(i);
				String[] pv = pathVersion.get(i).split("&");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				System.out.println("======================================similarity=" + sim
						+ "======================================");
				String out = util.readVerInfoFile(rootPath, version);
				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
				}
			}
		} else if (keyValue.size() != 0) {
			for (int i = 0; i < keyValue.size(); i++) {
				String[] kv = keyValue.get(i).split("=");
				if (j < keyValue.size()) {
					pathVersion.add(kv[0]);
					value.add(kv[1]);
				} else {
					break;
				}
				j++;
			}

			for (int i = 0; i < keyValue.size(); i++) {
				String sim = value.get(i);
				String[] pv = pathVersion.get(i).split("&");
				String rootPath = pv[0];
				int version = Integer.parseInt(pv[1]);
				System.out.println("======================================similarity=" + sim
						+ "======================================");
				String out = util.readVerInfoFile(rootPath, version);
				if (out.equals("$$")) {
					continue;
				} else {
					System.out.println(out);
				}
			}

		} else {
			System.out.println("null");
		}
	}
	
	public static void output(Map<Integer,Double> similarList, String rootPath){
		ArrayList<String> keyValue = util.sortSearchResult(similarList);		
		int j=0;
		ArrayList<String> version = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		if(keyValue.size()!=0){
			for (int i=0;i<keyValue.size();i++) {
			String[] kv = keyValue.get(i).split("=");
			if(j<10){
				version.add(kv[0]);	
				value.add(kv[1]);
			}else{
				break;
			}
			j++;				
		}
		
		for(int i=0;i<10;i++){
			String sim = value.get(i);
			System.out.println("======================================similarity="+sim+"======================================");	
			String out = util.readVerInfoFile(rootPath, Integer.parseInt(version.get(i)));
			if(out.equals("$$")){
				continue;
			}else{
				System.out.println(out);
			}			
		}
		}else{
			System.out.println("null");
		}

	}

	
	public static Map<Integer,Double> hashSearchEngine(String sourceBasePath,String tagartBasePath){
		ArrayList<String> versionList = new ArrayList<String>();
		Map<Integer,Double> similarList = new HashMap<Integer,Double>();
		double similar = 0.0;
		versionList = util.obtainMultiFileName(tagartBasePath);
		for(int i =0;i<versionList.size();i++){
			int tagartVerion = Integer.parseInt(versionList.get(i));
			similar = compareSimilar4Fragments(sourceBasePath,tagartBasePath,tagartVerion);
			similarList.put(tagartVerion, similar);
		}
		
		return similarList;
	}
	
	
	//hash code repetitive computing for code fragments 
	public static double compareSimilar4Fragments(String sourcePath,String tagartBasePath,int tagartVer) {
		double count = 0;
		//sourceVer = 117;
		//tagartVer = 132;
		Map<String, String> sourceFragmentHash = util.readFragmentHashFile(sourcePath);
		Map<String, String> tagartFragmentHash = util.readFragmentHashFile(tagartBasePath, tagartVer);
		ArrayList<String> sourcehash = new ArrayList<String>();
		ArrayList<String> tagarthash = new ArrayList<String>();

		for (Map.Entry<String, String> entry : sourceFragmentHash.entrySet()) {
			sourcehash.add(entry.getValue());
		}
		for (Map.Entry<String, String> entry2 : tagartFragmentHash.entrySet()) {
			tagarthash.add(entry2.getValue());
		}
		
		Map<String, Integer> maps = new HashMap<String, Integer>();
		for (int i = 0; i < sourcehash.size(); i++) {
			maps.put(sourcehash.get(i), 1);
		}
		for (int j = 0; j < tagarthash.size(); j++) {
			if (maps.containsKey(tagarthash.get(j))) {
				count++;
			}
		}
		return ((count)/(sourcehash.size() + tagarthash.size()-count));
	}
	
	
    public static double compareSyntaxSimilar4OriginalCodeFragments(String sourcePath,String tagartBasePath,int tagartVer){
		
		File f = new File(sourcePath);
		if(!f.exists()){
			System.out.println("Source path ["+sourcePath+"] is not existing");
			return -1;
		}
		
		Map<String, String> sourceFragmentHash = util.readFragmentHashFile(sourcePath);
		Map<String, String> tagartFragmentHash = util.readFragmentHashFile4OriginalCode(tagartBasePath, tagartVer);
		ArrayList<Long> sourcehash = new ArrayList<Long>();
		ArrayList<Long> tagarthash = new ArrayList<Long>();

		for (Map.Entry<String, String> entry : sourceFragmentHash.entrySet()) {
			sourcehash.add(Long.parseLong(entry.getValue()));
		}
		for (Map.Entry<String, String> entry2 : tagartFragmentHash.entrySet()) {
			tagarthash.add(Long.parseLong(entry2.getValue()));
		}
		Compare comp = new Compare(3,"");
		double similar = comp.codeFragmentHashCompare(sourcehash,tagarthash);
		

		return similar;
	}
	
	//Syntax Similar computing for  code fragments through hash code
	public static double compareSyntaxSimilar4Fragments(String sourcePath,String tagartBasePath,int tagartVer){
		
		File f = new File(sourcePath);
		if(!f.exists()){
			System.out.println("Source path ["+sourcePath+"] is not existing");
			return -1;
		}
		
		Map<String, String> sourceFragmentHash = util.readFragmentHashFile(sourcePath);
		Map<String, String> tagartFragmentHash = util.readFragmentHashFile(tagartBasePath, tagartVer);
		ArrayList<Long> sourcehash = new ArrayList<Long>();
		ArrayList<Long> tagarthash = new ArrayList<Long>();

		for (Map.Entry<String, String> entry : sourceFragmentHash.entrySet()) {
			sourcehash.add(Long.parseLong(entry.getValue()));
		}
		for (Map.Entry<String, String> entry2 : tagartFragmentHash.entrySet()) {
			tagarthash.add(Long.parseLong(entry2.getValue()));
		}
		Compare comp = new Compare(3,"");
		double similar = comp.codeFragmentHashCompare(sourcehash,tagarthash);
		

		return similar;
	}

}
