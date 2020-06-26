package cn.edu.sysu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.edu.sysu.config.ConfigOperation;

import java.util.TreeMap;


public class util {
	public static void main(String[] args){
		System.out.println(countJavaFiles("D:/data/jedit/257/old/src"));
		TreeMap<TreeMap<String,Integer>,Double> similarList = new TreeMap<TreeMap<String,Integer>,Double>();
		TreeMap<String,Integer> pathVersion1 = new TreeMap<String,Integer>();
		TreeMap<String,Integer> pathVersion2 = new TreeMap<String,Integer>();
		TreeMap<String,Integer> pathVersion3 = new TreeMap<String,Integer>();
		TreeMap<String,Integer> pathVersion4 = new TreeMap<String,Integer>();
		pathVersion1.put("D:/jedit", 23);
		pathVersion2.put("D:/jedit", 24);
		pathVersion3.put("D:/jedit", 26);
		pathVersion4.put("D:/jedit", 27);
		similarList.put(pathVersion1, 0.4);
		similarList.put(pathVersion2, 0.5);
		similarList.put(pathVersion3, 0.2);
		similarList.put(pathVersion4, 0.3);
		System.out.println(sortSearchResult1(similarList));
	}
	
	public static int countFiles(String filePath){
		File f = new File(filePath);
		String[] s = f.list();
		return s.length; 
	}
	
	//count .java files 
	public static int countJavaFiles(String filePath) {
		File inFile = new File(filePath);
		int sumFile = 0;// 总文件数
		for (File file : inFile.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".java")) {
				sumFile++;
			}
		}
		return sumFile;
	}
	
	//only one file in this file
	public static String obtainClassName(String filePath) {
		File file = new File(filePath);
		for (File f : file.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".java")) {
				return f.getName();
			}else{
				continue;
			}
		}
		return null;
	}
	
	//multiple files in this file
	public static ArrayList<String> obtainMultiFileName(String filePath) {
		ArrayList<String> filenameList = new ArrayList<String>();
		File file = new File(filePath);
		String[] fileName = file.list();
		for(int i=0;i<fileName.length;i++){
			filenameList.add(fileName[i]);
		}
		return filenameList;
	}
	
	public static void extractChangedCodeFragment4OriginalCode(String basePath, int ver) {
		String distillPath = basePath + "/" + ver + "/originalcode/changedistill/changedistill.txt";
		String hashPath = basePath + "/" + ver + "/originalcode/hash/hash.txt";

		TreeMap<String, String> hashmap = readHashFile(hashPath);

		TreeMap<String, String> distillmap = readDistillFile(distillPath);

		ArrayList<Integer> checkRepeatHash = new ArrayList<Integer>();
		TreeMap<Integer, Integer> hash4CodeFragment = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
			/*
			 * int compare(Object o1, Object o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2，
			 * 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
			 */
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		for (Map.Entry<String, String> entry : distillmap.entrySet()) {
			int startline = Integer.parseInt(entry.getKey());
			int endline = Integer.parseInt(entry.getValue());

			for (Map.Entry<String, String> entry2 : hashmap.entrySet()) {
				int linenum = Integer.parseInt(entry2.getKey());
				if (checkRepeatHash.contains(linenum)) {
					continue;
				} else {
					if (startline <= linenum && linenum <= endline) {
						int hashcode = Integer.parseInt(entry2.getValue());
						hash4CodeFragment.put(linenum, hashcode);
						checkRepeatHash.add(linenum);
					}
				}

			}
		}
		// sort by line number
		int linenum1 = 0;
		int hashcode1 = 0;
		for (Map.Entry<Integer, Integer> entry3 : hash4CodeFragment.entrySet()) {
			linenum1 = entry3.getKey();
			hashcode1 = entry3.getValue();
			writefile4OriginalCode(linenum1, hashcode1, basePath, ver);
		}

	}
	
	public static void extractChangedCodeFragment(String basePath, int ver) {
		String distillPath = basePath + "/" + ver + "/changedistill/changedistill.txt";
		String hashPath = basePath + "/" + ver + "/hash/hash.txt";

		TreeMap<String, String> hashmap = readHashFile(hashPath);

		TreeMap<String, String> distillmap = readDistillFile(distillPath);

		ArrayList<Integer> checkRepeatHash = new ArrayList<Integer>();
		TreeMap<Integer, Integer> hash4CodeFragment = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
			/*
			 * int compare(Object o1, Object o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2，
			 * 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
			 */
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		for (Map.Entry<String, String> entry : distillmap.entrySet()) {
			int startline = Integer.parseInt(entry.getKey());
			int endline = Integer.parseInt(entry.getValue());

			for (Map.Entry<String, String> entry2 : hashmap.entrySet()) {
				int linenum = Integer.parseInt(entry2.getKey());
				if (checkRepeatHash.contains(linenum)) {
					continue;
				} else {
					if (startline <= linenum && linenum <= endline) {
						int hashcode = Integer.parseInt(entry2.getValue());
						hash4CodeFragment.put(linenum, hashcode);
						checkRepeatHash.add(linenum);
					}
				}

			}
		}
		// sort by line number
		int linenum1 = 0;
		int hashcode1 = 0;
		for (Map.Entry<Integer, Integer> entry3 : hash4CodeFragment.entrySet()) {
			linenum1 = entry3.getKey();
			hashcode1 = entry3.getValue();
			writefile(linenum1, hashcode1, basePath, ver);
		}

	}
	
	public static ArrayList<String> extractChangedCodeIdentifier1(String basePath, int ver) {
		String distillPath = basePath + "/" + ver + "/changedistill/changedistill.txt";
		String identifierPath = basePath + "/" + ver + "/identifier/identifier.txt";

		TreeMap<String, ArrayList<String>> identifiermap = readIdentifierFile(identifierPath);

		TreeMap<String, String> distillmap = readDistillFile(distillPath);

		//ArrayList<Integer> checkRepeatHash = new ArrayList<Integer>();
		ArrayList<String> identifier4CodeFragment = new ArrayList<String>();
		for (Map.Entry<String, String> entry : distillmap.entrySet()) {
			int startline = Integer.parseInt(entry.getKey());
			int endline = Integer.parseInt(entry.getValue());

			for (Map.Entry<String, ArrayList<String>> entry2 : identifiermap.entrySet()) {
				int linenum = Integer.parseInt(entry2.getKey());
					if (startline <= linenum && linenum <= endline) {
						ArrayList<String> hashcode = entry2.getValue();
						identifier4CodeFragment.addAll(hashcode);					
					}
			}
		}
		return identifier4CodeFragment;
	}
	
	public static String extractChangedCodeIdentifier2(String basePath, int ver, ArrayList<String> filterWordsList) {

		String distillPath = basePath + "/" + ver + "/changedistill/changedistill.txt";
		String identifierPath = basePath + "/" + ver + "/identifier/identifier.txt";

		TreeMap<String, ArrayList<String>> identifiermap = readIdentifierFile(identifierPath);

		TreeMap<String, String> distillmap = readDistillFile(distillPath);

		//ArrayList<Integer> checkRepeatHash = new ArrayList<Integer>();
		StringBuffer identifier4CodeFragment = new StringBuffer();
		for (Map.Entry<String, String> entry : distillmap.entrySet()) {
			int startline = Integer.parseInt(entry.getKey());
			int endline = Integer.parseInt(entry.getValue());

			for (Map.Entry<String, ArrayList<String>> entry2 : identifiermap.entrySet()) {
				int linenum = Integer.parseInt(entry2.getKey());
					if (startline <= linenum && linenum <= endline) {
						ArrayList<String> hashcode = entry2.getValue();
						hashcode.removeAll(filterWordsList);
						for(int i= 0; i<hashcode.size(); i++){
							identifier4CodeFragment.append(hashcode.get(i));
							identifier4CodeFragment.append("&");
						}

									
					}
			}
		}
		if(identifier4CodeFragment.length()>1){
			identifier4CodeFragment.deleteCharAt(identifier4CodeFragment.length()-1);		
		}
		return identifier4CodeFragment.toString();
	}
	
	
	public static TreeMap<Integer, String> extractChangedCodeIdentifier(String basePath, int ver) {
		String distillPath = basePath + "/" + ver + "/changedistill/changedistill.txt";
		String identifierPath = basePath + "/" + ver + "/identifier/identifier.txt";

		TreeMap<String, String> identifiermap = readHashFile(identifierPath);

		TreeMap<String, String> distillmap = readDistillFile(distillPath);

		//ArrayList<Integer> checkRepeatHash = new ArrayList<Integer>();
		TreeMap<Integer, String> identifier4CodeFragment = new TreeMap<Integer, String>(new Comparator<Integer>() {
			/*
			 * int compare(Object o1, Object o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2，
			 * 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
			 */
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		for (Map.Entry<String, String> entry : distillmap.entrySet()) {
			int startline = Integer.parseInt(entry.getKey());
			int endline = Integer.parseInt(entry.getValue());

			for (Map.Entry<String, String> entry2 : identifiermap.entrySet()) {
				int linenum = Integer.parseInt(entry2.getKey());
					if (startline <= linenum && linenum <= endline) {
						String hashcode = entry2.getValue();
						identifier4CodeFragment.put(linenum, hashcode);
					
					}
			}
		}
		return identifier4CodeFragment;
	}
	
	public static ArrayList<String> readChangeTypesinDistillFile(String basePath, int ver){
		String absolutePath = basePath + "/" + ver + "/changedistill/changedistill.txt";
		File f2 = new File(absolutePath);
		if(!f2.exists() || f2.length() == 0){
			return null;
		}
		ArrayList<String> result = new ArrayList<String>(); 

		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					result.add(strMatrix[0]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return result;
	}

	public static TreeMap<String,String> readDistillFile(String absolutePath){
		TreeMap<String,String> map=new TreeMap<String,String>(new Comparator<String>(){   
            /* 
             * int compare(Object o1, Object o2) 返回一个基本类型的整型， 
             * 返回负数表示：o1 小于o2， 
             * 返回0 表示：o1和o2相等， 
             * 返回正数表示：o1大于o2。 
             */  
            public int compare(String o1, String o2) {                
                return o1.compareTo(o2);  
            }     
        }); 

		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					map.put(strMatrix[1],strMatrix[2]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static TreeMap<String,ArrayList<String>> readIdentifierFile(String absolutePath){
		TreeMap<String,ArrayList<String>> map = new TreeMap<String,ArrayList<String>>();
		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					if(map.containsKey(strMatrix[0])){
						map.get(strMatrix[0]).add(strMatrix[1]);						
					}else{
						ArrayList<String> list = new ArrayList<String>();
						list.add(strMatrix[1]);
						map.put(strMatrix[0],list);	
					}
									
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static TreeMap<String,String> readHashFile(String absolutePath){
		TreeMap<String,String> map=new TreeMap<String,String>(new Comparator<String>(){   
            /* 
             * int compare(Object o1, Object o2) 返回一个基本类型的整型， 
             * 返回负数表示：o1 小于o2， 
             * 返回0 表示：o1和o2相等， 
             * 返回正数表示：o1大于o2。 
             */  
            public int compare(String o1, String o2) {                
                return o1.compareTo(o2);  
            }     
        }); 
		
		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					map.put(strMatrix[0],strMatrix[1]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static Map<String,String> readFragmentHashFile4OriginalCode(String basePath, int ver){
		String fragmenthashPath = basePath+"/"+ver+"/originalcode/fragmenthash/fragmenthash.txt";
		Map<String,String> map=new HashMap<String,String>();   
		if(!fragmenthashPath.equals("")&!fragmenthashPath.equals(null)){
			File file = new File(fragmenthashPath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					map.put(strMatrix[0],strMatrix[1]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static Map<String,String> readFragmentHashFile(String basePath, int ver){
		String fragmenthashPath = basePath+"/"+ver+"/fragmenthash/fragmenthash.txt";
		Map<String,String> map=new HashMap<String,String>();   
		if(!fragmenthashPath.equals("")&!fragmenthashPath.equals(null)){
			File file = new File(fragmenthashPath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					map.put(strMatrix[0],strMatrix[1]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static Map<String,String> readFragmentHashFile(String absolutePath){
		String fragmenthashPath = absolutePath;
		Map<String,String> map=new HashMap<String,String>();   
		if(!fragmenthashPath.equals("")&!fragmenthashPath.equals(null)){
			File file = new File(fragmenthashPath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");
					map.put(strMatrix[0],strMatrix[1]);					
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static String readFragmentHashFile2(String absolutePath){
		StringBuffer stringBuffer = new StringBuffer();
		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");	
					stringBuffer.append(strMatrix[1]);
					stringBuffer.append("&");
				}
				if(stringBuffer.length()>1){
					stringBuffer.deleteCharAt(stringBuffer.length()-1);
				}				
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}else{
			stringBuffer.append("inviald path");
		}
		return stringBuffer.toString();
	}
	
	public static String readVerInfoFile(String rootPath,int ver){		
		String path = rootPath+"/"+ver+"/new/src/"+ver+".txt";
		
		File f = new File(path);
		if(!f.exists()){
			System.out.println("Source path ["+path+"] is not existing");
			return "$$";
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		if(!path.equals("")&!path.equals(null)){
			File file = new File(path);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"GBK");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					stringBuffer.append(str);
					stringBuffer.append("\n");
				}
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}else{
			stringBuffer.append("inviald path");
		}
		return stringBuffer.toString();
	}
	
	public static String readFragmentHash(String absolutePath){
		StringBuffer stringBuffer = new StringBuffer();
		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					String[] strMatrix = str.split(":");	
					stringBuffer.append(strMatrix[1]);
					stringBuffer.append("&");
				}
				if(stringBuffer.length()>1){
					stringBuffer.deleteCharAt(stringBuffer.length()-1);
				}
				
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}else{
			stringBuffer.append("inviald path");
		}
		return stringBuffer.toString();
	}
	
	public static String readFile(String absolutePath){
		StringBuffer stringBuffer = new StringBuffer();
		if(!absolutePath.equals("")&!absolutePath.equals(null)){
			File file = new File(absolutePath);
			try {
				FileInputStream fileInputStream =new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while((str = bufferedReader.readLine())!=null){
					stringBuffer.append(str);
					stringBuffer.append("\n");
				}
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}else{
			stringBuffer.append("inviald path");
		}
		return stringBuffer.toString();
	}
	
	public static void writefile4OriginalCode(int linenum, int hashcode, String basePath, int verNum){
		File dir1 = new File(basePath+"/"  + verNum + "/originalcode/fragmenthash/");
		if (!dir1.exists()) {
			dir1.mkdirs();
		}
		File file = new File(dir1.getPath() + "/fragmenthash.txt");
		try {
			 FileOutputStream fos = new FileOutputStream(file,true);
		     OutputStreamWriter osw = new OutputStreamWriter(fos);
		     BufferedWriter bw = new BufferedWriter(osw);			   				
		     bw.write(linenum+":"+hashcode+"\r\n");
		     bw.flush();
		     bw.close();
		     osw.close();
		     fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writefile(int linenum, int hashcode, String basePath, int verNum){
		File dir1 = new File(basePath+"/"  + verNum + "/fragmenthash");
		if (!dir1.exists()) {
			dir1.mkdirs();
		}
		File file = new File(dir1.getPath() + "/fragmenthash.txt");
		try {
			 FileOutputStream fos = new FileOutputStream(file,true);
		     OutputStreamWriter osw = new OutputStreamWriter(fos);
		     BufferedWriter bw = new BufferedWriter(osw);			   				
		     bw.write(linenum+":"+hashcode+"\r\n");
		     bw.flush();
		     bw.close();
		     osw.close();
		     fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeSearchResult(String sourceBasePath, String content, String resultPath, int sourceVersion){
		File dir1 = new File(resultPath);
		if (!dir1.exists()) {
			dir1.mkdirs();
		}
		String[] projName= sourceBasePath.split("\\\\");
		if(projName.length<2){
			projName = null;
			projName = sourceBasePath.split("/");
		}
		File file = new File(dir1.getPath() + "/"+projName[2]+"-results.txt");
		try {
			 FileOutputStream fos = new FileOutputStream(file,true);
		     OutputStreamWriter osw = new OutputStreamWriter(fos,"GBK");
		     BufferedWriter bw = new BufferedWriter(osw);			   				
		     bw.write(content+"\r\n");
		     bw.flush();
		     bw.close();
		     osw.close();
		     fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//check whether a specfic file existing
	public static boolean isFileExisting(String path,int ver){
		File file=new File(path+"/"+ver+"/fragmenthash");
		if(file.exists()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static ArrayList<String> sortSearchResult2(TreeMap<TreeMap<String,Integer>,Double> similarList){
		List<Map.Entry<TreeMap<String,Integer>,Double>> infoIds = new ArrayList<Map.Entry<TreeMap<String,Integer>,Double>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<TreeMap<String,Integer>,Double>>() {   
		    public int compare(Map.Entry<TreeMap<String,Integer>,Double> o1, Map.Entry<TreeMap<String,Integer>,Double> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		        //return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		ArrayList<String> keyValue = new ArrayList<String>();
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    keyValue.add(id);
		    System.out.println(id);
		}
		return keyValue;
	}
	
	public static ArrayList<String> sortSearchResult1(TreeMap<TreeMap<String,Integer>,Double> similarList){
		List<Map.Entry<TreeMap<String,Integer>,Double>> infoIds = new ArrayList<Map.Entry<TreeMap<String,Integer>,Double>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<TreeMap<String,Integer>,Double>>() {   
		    public int compare(Map.Entry<TreeMap<String,Integer>,Double> o1, Map.Entry<TreeMap<String,Integer>,Double> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		        //return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		ArrayList<String> keyValue = new ArrayList<String>();
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    keyValue.add(id);
		    System.out.println(id);
		}
		return keyValue;
	}
	
	public static ArrayList<String> sortSearchResult2(Map<String,String> similarList){
		List<Map.Entry<String,String>> infoIds = new ArrayList<Map.Entry<String,String>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String,String>>() {   
		    public int compare(Map.Entry<String,String> o1, Map.Entry<String,String> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		        //return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		ArrayList<String> keyValue = new ArrayList<String>();
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    keyValue.add(id);
		    System.out.println(id);
		}
		return keyValue;
	}
	
	public static ArrayList<String> sortSearchResult1(Map<String,Double> similarList){
		List<Map.Entry<String,Double>> infoIds = new ArrayList<Map.Entry<String,Double>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String,Double>>() {   
		    public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		        //return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		ArrayList<String> keyValue = new ArrayList<String>();
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    keyValue.add(id);
		    System.out.println(id);
		}
		return keyValue;
	}
	
	public static ArrayList<String> sortSearchResult(Map<Integer,Double> similarList){
		List<Map.Entry<Integer,Double>> infoIds = new ArrayList<Map.Entry<Integer,Double>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<Integer,Double>>() {   
		    public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		        //return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		ArrayList<String> keyValue = new ArrayList<String>();
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    keyValue.add(id);
		    System.out.println(id);
		}
		return keyValue;
	}
	
	public static Map<String,String> sortMap(Map<String,String> similarList){
		List<Map.Entry<String,String>> infoIds = new ArrayList<Map.Entry<String,String>>(similarList.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String,String>>() {   
		    public int compare(Map.Entry<String,String> o1, Map.Entry<String,String> o2) {      
		        //return (o2.getValue().compareTo(o1.getValue())); 
		        return (o1.getKey()).compareTo(o2.getKey());
		    }
		}); 
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    System.out.println(id);
		}
		return similarList;
	}
	
	public static Map<String, String> sortHashMap(Map<String, String> hashMap) {
		Map<String, String> map = new TreeMap<String, String>();
		Object[] key_arr = hashMap.keySet().toArray();
		int array[] = new int[key_arr.length];
		for (int i = 0; i < key_arr.length; i++) {
			array[i] = Integer.parseInt(key_arr[i].toString());
		}
		Arrays.sort(array);
		Object array2[] = new Object[key_arr.length];
		for (int i = 0; i < key_arr.length; i++) {
			array2[i] = String.valueOf(array[i]);
		}
		for (Object key : array2) {
			Object value = hashMap.get(key);
			map.put(key.toString(), value.toString());
		}
		return map;
	}
	
	public static void clearTxtFile(String path){
		try {
			File file = new File(path);
			FileWriter fw =  new FileWriter(file);
			fw.write("");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static ArrayList<Integer> calculateSimilarDistribution(ArrayList<Double> similarAll){
		ArrayList<Integer> distribution = new ArrayList<Integer>();
		double similar =0;
		int similar1 =0;
		int similar2 =0;
		int similar3 =0;
		int similar4 =0;
		int similar5 =0;
		int similar6 =0;
		int similar7 =0;
		int similar8 =0;
		int similar9 =0;
		int similar10 =0;
		for(int i = 0; i<similarAll.size(); i++){
			similar = similarAll.get(i);
			if(similar>0 && similar<=0.1){
				similar1++;
			}else if(similar>0.1 && similar<=0.2){
				similar2++;
			}else if(similar>0.2 && similar<=0.3){
				similar3++;
			}else if(similar>0.3 && similar<=0.4){
				similar4++;
			}else if(similar>0.4 && similar<=0.5){
				similar5++;
			}else if(similar>0.5 && similar<=0.6){
				similar6++;
			}else if(similar>0.6 && similar<=0.7){
				similar7++;
			}else if(similar>0.7 && similar<=0.8){
				similar8++;
			}else if(similar>0.8 && similar<=0.9){
				similar9++;
			}else if(similar>0.9 && similar<=1){
				similar10++;
			}
		}
		distribution.add(similar1);
		distribution.add(similar2);
		distribution.add(similar3);
		distribution.add(similar4);
		distribution.add(similar5);
		distribution.add(similar6);
		distribution.add(similar7);
		distribution.add(similar8);
		distribution.add(similar9);
		distribution.add(similar10);
		return distribution;		
	}

}
