package cn.edu.sysu;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.edu.sysu.config.ConfigOperation;
import cn.edu.sysu.searchengine.FragmentHashSearchEngine;
import cn.edu.sysu.semanticsimilar.SemanticSimilar4Identifier;
import cn.edu.sysu.syntaxsimilar.Compare;

public class SearchMainClass {

	public static void main(String[] args){
		String sourceBasePath = ConfigOperation.getConfigProperties("basePath7");
		String resultPath = ConfigOperation.getConfigProperties("resultPath");
		int startVer = Integer.parseInt(ConfigOperation.getConfigProperties("startVer"));
		int endVer = Integer.parseInt(ConfigOperation.getConfigProperties("endVer"));
		File dir0 = new File(resultPath);
		if (!dir0.exists()) {
			dir0.mkdirs();
		}
		String[] projName= sourceBasePath.split("\\\\");
		File file0 = new File(dir0.getPath() + "/"+projName[2]+"-results.txt");
		//firstly clear results.txt ;
		util.clearTxtFile(file0.getAbsolutePath());
		//load hashs for all fragments
		HashMap<String, String> hashs4AllFragments = loadHashs4AllFragments();
		//load identifiers for all fragments
		HashMap<String, String> identifiers4AllFragments = loadIdentifiers4AllFragments();
		//load hashs for all original fragments
		HashMap<String, String> hashs4AllOriginalFragments = loadHashs4AllOriginalFragments();
		for(int ver = startVer; ver<endVer; ver++){
			//String path = sourceBasePath+"/"+ver+"/fragmenthash/fragmenthash.txt";
			int retu = search2(resultPath, sourceBasePath, ver, hashs4AllFragments, identifiers4AllFragments, hashs4AllOriginalFragments);
			//search(resultPath, sourceBasePath, ver);
			if(retu == 0){
				continue;
			}
		}
		
		//testSearch();
	}
	
	//based on syntax similar, semantic similar, and syntax similar for original code 
	public static int search(String resultPath, String sourceBasePath, int sourceVersion){
		// TODO Auto-generated method stub
		//<jedit,7515><jedit,24067>
		//sourceBasePath = "D:/data/jedit/";
		//sourceVersion = 24067;		
		String sourcePath = sourceBasePath+"/"+sourceVersion+"/fragmenthash/fragmenthash.txt";
		String originalCodeSourcePath = sourceBasePath+"/"+sourceVersion+"/originalcode/fragmenthash/fragmenthash.txt";
		double semanticWeight = Double.parseDouble(ConfigOperation.getConfigProperties("semanticWeight"));
		double syntaxWeight = Double.parseDouble(ConfigOperation.getConfigProperties("syntaxWeight"));
		String searchPath = ConfigOperation.getConfigProperties("searchPath");
		String[] allSearchPath = searchPath.split("&");
		String rootPath = null;		
		TreeMap<String,String> multiSimilarList = new TreeMap<String,String>();
		DecimalFormat df = new DecimalFormat("#.00000");
		
			File f = new File(sourcePath);
			if(f.length ()==0){
				System.out.println("Source file ["+sourcePath+"] is null, search is terminated");
				return 0;
			}
		 
		if(allSearchPath.length!=0){
			for(int i = 0; i<allSearchPath.length; i++){
				ArrayList<String> fileName = new ArrayList<String>();
				ArrayList<Integer> numfileName = new ArrayList<Integer>();
				rootPath = allSearchPath[i];
				//int commitNum = util.countFiles(rootPath);
				fileName = util.obtainMultiFileName(rootPath);
				for(int t = 0;t<fileName.size();t++){
					numfileName.add(Integer.parseInt(fileName.get(t)));
				}
				Collections.sort(numfileName);
				int commitNum = numfileName.get(numfileName.size()-1);
				
				for (int ver = 1; ver < commitNum; ver++){
					double similar = 0.0;
					double syntaxSimilar = 0.0;
					double syntaxSimilar4OriginalCode = 0.0;
					double semanticSimilar = 0.0;
					String basePath1 = rootPath + "/" + ver + "/new/src/";
					String basePath2 = rootPath + "/" + ver + "/old/src/";
					
					File f1 = new File(basePath1);
					File f2 = new File(basePath2);
					if(!f1.exists() || !f2.exists()){
						continue;
					}

					if ((util.countJavaFiles(basePath1) == 1) && (util.countJavaFiles(basePath2) == 1)){
						if(util.isFileExisting(rootPath,ver)){
							syntaxSimilar = FragmentHashSearchEngine.compareSyntaxSimilar4Fragments(sourcePath,rootPath,ver);	
							semanticSimilar = SemanticSimilar4Identifier.semanticSimilar4FragmentIdentifier(sourceBasePath,sourceVersion,rootPath,ver);
							if(syntaxSimilar == -1){//source path is not existing
								break;
							}
							File file1=new File(originalCodeSourcePath);
							File file2=new File(rootPath+ver+"/originalcode/fragmenthash/fragmenthash.txt");
							if(file1.exists() && file2.exists() && syntaxSimilar!=0){
								syntaxSimilar4OriginalCode = FragmentHashSearchEngine.compareSyntaxSimilar4OriginalCodeFragments(originalCodeSourcePath,rootPath,ver);
							}
						
							
							if(syntaxSimilar!=0 && syntaxSimilar4OriginalCode==0){
								similar = (syntaxSimilar*syntaxWeight)+(semanticSimilar*semanticWeight);
								multiSimilarList.put(rootPath+"&"+ver, df.format(similar)+"&"+df.format(syntaxSimilar)+"&"+df.format(semanticSimilar));
								//similarList.put(rootPath+"&"+ver, Double.parseDouble(df.format(similar)));
								System.out.println(rootPath+"->"+ver+":TotalSimilarity:"+df.format(similar)+"   SyntaxSimilarity:"+df.format(syntaxSimilar)+"   SemanticSimilarity:"+df.format(semanticSimilar));
							} else if(syntaxSimilar!=0 && syntaxSimilar4OriginalCode!=0){
								if((syntaxSimilar+syntaxSimilar4OriginalCode)<1){
									similar = ((syntaxSimilar+syntaxSimilar4OriginalCode)*syntaxWeight)+(semanticSimilar*semanticWeight);
								}else{
									similar = (1*syntaxWeight)+(semanticSimilar*semanticWeight);
								}
								
								multiSimilarList.put(rootPath+"&"+ver, df.format(similar)+"&"+df.format(syntaxSimilar)+"&"+df.format(semanticSimilar)+"&"+df.format(syntaxSimilar4OriginalCode));
								//similarList.put(rootPath+"&"+ver, Double.parseDouble(df.format(similar)));
								System.out.println(rootPath+"->"+ver+":TotalSimilarity:"+df.format(similar)+"   SyntaxSimilarity:"+df.format(syntaxSimilar)+"   SemanticSimilarity:"+df.format(semanticSimilar)+"   syntaxSimilar4OriginalCode:"+df.format(syntaxSimilar4OriginalCode));
							}
							
						}else{
							continue;
						}
						
					}else{
						continue;
					}
				}
			}	
			System.out.println("Total number of code fragments:"+multiSimilarList.size());
			//FragmentHashSearchEngine.output(similarList);
			FragmentHashSearchEngine.outputSimilar(resultPath, multiSimilarList, sourceBasePath,  sourceVersion);
		}else{
			System.out.println("search path is null!!");
		}
		return 1;
	}
	
	//based on syntax and semantic Similar
	public static int search2(String resultPath, String sourceBasePath, int sourceVersion, HashMap<String, String> hashs4AllFragments, HashMap<String, String> identifier4AllFragments, HashMap<String, String> hashs4AllOriginalFragments){
		String sourcePath4Fragmenthash = sourceBasePath+"/"+sourceVersion+"/fragmenthash/fragmenthash.txt";
		String sourcePath4OriginalFragmenthash = sourceBasePath+"/"+sourceVersion+"/originalcode/fragmenthash/fragmenthash.txt";
		
		File f = new File(sourcePath4Fragmenthash);
		if(f.length ()==0){
			System.out.println("Source file ["+sourcePath4Fragmenthash+"] is null, search is terminated");
			return 0;
		}
		
		TreeMap<String,String> similarList = new TreeMap<String,String>();
		
		String sourceFragmentHash = util.readFragmentHashFile2(sourcePath4Fragmenthash);
		ArrayList<Long> sourceHash = new ArrayList<Long>();
		String[] sourceHash4Fragment = sourceFragmentHash.split("&");
		for (int i = 0; i<sourceHash4Fragment.length;i++) {
			sourceHash.add(Long.parseLong(sourceHash4Fragment[i]));
		}
		
		String filterWords = ConfigOperation.getConfigProperties("filterWords");
		String[] filterWordsArray = filterWords.split("&");
		ArrayList<String> filterWordsList = new ArrayList<String>(Arrays.asList(filterWordsArray));
		ArrayList<String> sourceIdentifier = new ArrayList<String>();
		String sourceIdentifier4Str = new String();
		sourceIdentifier4Str = util.extractChangedCodeIdentifier2(sourceBasePath, sourceVersion, filterWordsList);
		String[] sourceIdentifier4Matrix = sourceIdentifier4Str.split("&");
		for(int i=0;i<sourceIdentifier4Matrix.length;i++){
			sourceIdentifier.add(sourceIdentifier4Matrix[i]);
		}
		File file = new File(sourcePath4OriginalFragmenthash);
		if(file.exists() && file.length() != 0){
			String sourceOriginalFragmentHash = util.readFragmentHashFile2(sourcePath4OriginalFragmenthash);
			ArrayList<Long> sourceOriginalHash = new ArrayList<Long>();
			String[] sourceHash4OriginalFragment = sourceOriginalFragmentHash.split("&");
			for (int i = 0; i<sourceHash4OriginalFragment.length;i++) {
				sourceOriginalHash.add(Long.parseLong(sourceHash4OriginalFragment[i]));
			}			
			similarList = compareSimilar(hashs4AllFragments, sourceHash, identifier4AllFragments, sourceIdentifier, hashs4AllOriginalFragments,sourceOriginalHash);
		}else{
			similarList = compareSimilar(hashs4AllFragments, sourceHash, identifier4AllFragments, sourceIdentifier);
		}
		
		
		ArrayList<String> keyValue = util.sortSearchResult2(similarList);
		for (int i=0;i<keyValue.size();i++) {
			System.out.println(keyValue.get(i));
		}
		FragmentHashSearchEngine.outputSimilar2(resultPath,similarList,sourceBasePath,sourceVersion);
		return 1;
	}
	
	public static TreeMap<String,String> compareSimilar(HashMap<String, String> hashs4AllFragments, ArrayList<Long> sourceHash, HashMap<String, String> identifier4AllFragments, ArrayList<String> sourceIdentifier, HashMap<String, String> hashs4AllOriginalFragments, ArrayList<Long> sourceOriginalHash){
		DecimalFormat df = new DecimalFormat("#.00000");
		double minRepetitive = Double.parseDouble(ConfigOperation.getConfigProperties("minRepetitive"));
		TreeMap<String,String> similarList = new TreeMap<String,String>();				
		Map<Long, Integer> maps = new HashMap<Long, Integer>();
		for (int i = 0; i < sourceHash.size(); i++) {
			maps.put(sourceHash.get(i), 1);
		}
		for (Map.Entry<String, String> entry : hashs4AllFragments.entrySet()) {
			double similar = 0;
			double syntaxSimilar = 0;
			double semanticSimilar =0;
			double count = 0;
			double repetitive = 0;
			double syntaxSimilar4OriginalFragment = 0;
			String hashcode = "";
			ArrayList<Long> tagartHash = new ArrayList<Long>();
			
			ArrayList<String> tagartIdentifier = new ArrayList<String>();
			String rootPath_ver = entry.getKey();
			String identifier = identifier4AllFragments.get(rootPath_ver);
			String[] identifierMatrix = identifier.split("&");
			for(int i = 0; i<identifierMatrix.length;i++){
				tagartIdentifier.add(identifierMatrix[i]);
			}
			
			hashcode = entry.getValue();		
			String[] hashcodeMatrix = hashcode.split("&");
			for(int i=0; i<hashcodeMatrix.length; i++){
				tagartHash.add(Long.parseLong(hashcodeMatrix[i]));
			}			
			for (int j = 0; j < tagartHash.size(); j++) {
				if (maps.containsKey(tagartHash.get(j))) {
					count++;
				}
			}
			repetitive = ((count)/(sourceHash.size() + tagartHash.size()-count));
			System.out.println("repetitive:"+repetitive);			
			if(repetitive < minRepetitive){
				continue;
			} else {
				Compare comp = new Compare(3, "");
				syntaxSimilar = comp.codeFragmentHashCompare(sourceHash, tagartHash);
				semanticSimilar =  SemanticSimilar4Identifier.semanticSimilar4FragmentIdentifier2(sourceIdentifier,tagartIdentifier);
				
				ArrayList<Long> tagartOriginalHash = new ArrayList<Long>();
				String rootPathVer = entry.getKey();
				String hashs4OriginalFragments = hashs4AllOriginalFragments.get(rootPathVer);
				if(hashs4OriginalFragments != null){
					String[] hashs4OriginalFragmentsMatrix = hashs4OriginalFragments.split("&");
				    for(int i = 0; i<hashs4OriginalFragmentsMatrix.length;i++){
				    	tagartOriginalHash.add(Long.parseLong(hashs4OriginalFragmentsMatrix[i]));
				    }
				    Compare comp2 = new Compare(3, "");
				    syntaxSimilar4OriginalFragment = comp2.codeFragmentHashCompare(sourceOriginalHash, tagartOriginalHash);
				}else{
					syntaxSimilar4OriginalFragment = 0.0;
				}
				
				if (syntaxSimilar != 0) {
					similarList.put(rootPath_ver, String.valueOf(df.format(syntaxSimilar))+"-----semanticSimilar:"+String.valueOf(df.format(semanticSimilar))+"-----syntaxSimilar4OriginalFragment:"+String.valueOf(df.format(syntaxSimilar4OriginalFragment)));
					//System.out.println(rootPath_ver + ":" + df.format(similar));
				}
			}

		}
		return similarList;
	}
	
	public static TreeMap<String,String> compareSimilar(HashMap<String, String> hashs4AllFragments, ArrayList<Long> sourceHash, HashMap<String, String> identifier4AllFragments, ArrayList<String> sourceIdentifier){
		DecimalFormat df = new DecimalFormat("#.00000");
		double minRepetitive = Double.parseDouble(ConfigOperation.getConfigProperties("minRepetitive"));
		TreeMap<String,String> similarList = new TreeMap<String,String>();				
		Map<Long, Integer> maps = new HashMap<Long, Integer>();
		for (int i = 0; i < sourceHash.size(); i++) {
			maps.put(sourceHash.get(i), 1);
		}
		for (Map.Entry<String, String> entry : hashs4AllFragments.entrySet()) {
			double similar = 0;
			double syntaxSimilar = 0;
			double semanticSimilar =0;
			double count = 0;
			double repetitive = 0;
			String hashcode = "";
			ArrayList<Long> tagartHash = new ArrayList<Long>();
			
			ArrayList<String> tagartIdentifier = new ArrayList<String>();
			String rootPath_ver = entry.getKey();
			String identifier = identifier4AllFragments.get(rootPath_ver);
			String[] identifierMatrix = identifier.split("&");
			for(int i = 0; i<identifierMatrix.length;i++){
				tagartIdentifier.add(identifierMatrix[i]);
			}
			
			hashcode = entry.getValue();		
			String[] hashcodeMatrix = hashcode.split("&");
			for(int i=0; i<hashcodeMatrix.length; i++){
				tagartHash.add(Long.parseLong(hashcodeMatrix[i]));
			}			
			for (int j = 0; j < tagartHash.size(); j++) {
				if (maps.containsKey(tagartHash.get(j))) {
					count++;
				}
			}
			repetitive = ((count)/(sourceHash.size() + tagartHash.size()-count));
			System.out.println("repetitive:"+repetitive);			
			if(repetitive < minRepetitive){
				continue;
			} else {
				Compare comp = new Compare(3, "");
				syntaxSimilar = comp.codeFragmentHashCompare(sourceHash, tagartHash);
				semanticSimilar =  SemanticSimilar4Identifier.semanticSimilar4FragmentIdentifier2(sourceIdentifier,tagartIdentifier);				
				if (syntaxSimilar != 0) {
					similarList.put(rootPath_ver, String.valueOf(df.format(syntaxSimilar))+"-----semanticSimilar:"+String.valueOf(df.format(semanticSimilar)));
					//System.out.println(rootPath_ver + ":" + df.format(similar));
				}
			}

		}
		return similarList;
	}
	
	public static HashMap<String, String> loadHashs4AllFragments() {
		HashMap<String, String> hashs4AllFragments = new HashMap<String, String>();
		String searchPath = ConfigOperation.getConfigProperties("searchPath");
		String[] allSearchPath = searchPath.split("&");
		String rootPath = null;
		if (allSearchPath.length != 0) {
			for (int i = 0; i < allSearchPath.length; i++) {
				ArrayList<String> fileName = new ArrayList<String>();
				ArrayList<Integer> numfileName = new ArrayList<Integer>();
				rootPath = allSearchPath[i];
				fileName = util.obtainMultiFileName(rootPath);
				for (int t = 0; t < fileName.size(); t++) {
					numfileName.add(Integer.parseInt(fileName.get(t)));
				}
				Collections.sort(numfileName);
				int commitNum = numfileName.get(numfileName.size() - 1);

				for (int ver = 1; ver < commitNum; ver++) {

					String fragmenthashPath = rootPath + "/" + ver + "/fragmenthash/fragmenthash.txt";

					File file = new File(fragmenthashPath);

					if (!file.exists() || file.length() == 0) {
						continue;
					} else {
						String rootKey = rootPath + "/" + ver;
						String hashValue = util.readFragmentHash(fragmenthashPath);
						hashs4AllFragments.put(rootKey, hashValue);  
						System.out.println(rootKey+":"+hashValue);
					}
				}
			}
		} else {
			System.out.println("searchPath is null");
		}
		return hashs4AllFragments;
	}
	
	public static HashMap<String, String> loadIdentifiers4AllFragments() {

		HashMap<String, String> identifier4AllFragments = new HashMap<String, String>();
		String searchPath = ConfigOperation.getConfigProperties("searchPath");
		String[] allSearchPath = searchPath.split("&");
		String rootPath = null;
		if (allSearchPath.length != 0) {
			for (int i = 0; i < allSearchPath.length; i++) {
				ArrayList<String> fileName = new ArrayList<String>();
				ArrayList<Integer> numfileName = new ArrayList<Integer>();
				rootPath = allSearchPath[i];
				fileName = util.obtainMultiFileName(rootPath);
				for (int t = 0; t < fileName.size(); t++) {
					numfileName.add(Integer.parseInt(fileName.get(t)));
				}
				Collections.sort(numfileName);
				int commitNum = numfileName.get(numfileName.size() - 1);

				for (int ver = 1; ver < commitNum; ver++) {
					
					String filterWords = ConfigOperation.getConfigProperties("filterWords");
					String[] filterWordsArray = filterWords.split("&");
					ArrayList<String> filterWordsList = new ArrayList<String>(Arrays.asList(filterWordsArray));

					String fragmenthashPath = rootPath + "/" + ver + "/fragmenthash/fragmenthash.txt";

					File file = new File(fragmenthashPath);

					if (!file.exists() || file.length() == 0) {
						continue;
					} else {
						String rootVerKey = rootPath + "/" + ver;
						String identifierValue = util.extractChangedCodeIdentifier2(rootPath,ver,filterWordsList);
						identifier4AllFragments.put(rootVerKey, identifierValue);
						System.out.println(rootVerKey);
					}
				}
			}
		} else {
			System.out.println("searchPath is null");
		}
		return identifier4AllFragments;
	}
	
	public static HashMap<String, String> loadHashs4AllOriginalFragments(){
		int count = 0;
		HashMap<String, String> hashs4AllOriginalFragments = new HashMap<String, String>();
		String searchPath = ConfigOperation.getConfigProperties("searchPath");
		String[] allSearchPath = searchPath.split("&");
		String rootPath = null;
		if (allSearchPath.length != 0) {
			for (int i = 0; i < allSearchPath.length; i++) {
				ArrayList<String> fileName = new ArrayList<String>();
				ArrayList<Integer> numfileName = new ArrayList<Integer>();
				rootPath = allSearchPath[i];
				fileName = util.obtainMultiFileName(rootPath);
				for (int t = 0; t < fileName.size(); t++) {
					numfileName.add(Integer.parseInt(fileName.get(t)));
				}
				Collections.sort(numfileName);
				int commitNum = numfileName.get(numfileName.size() - 1);

				for (int ver = 1; ver < commitNum; ver++) {

					String fragmenthashPath = rootPath + "/" + ver + "/originalcode/fragmenthash/fragmenthash.txt";

					File file = new File(fragmenthashPath);

					if (!file.exists() || file.length() == 0) {
						continue;
					} else {
						String rootKey = rootPath + "/" + ver;
						String hashValue = util.readFragmentHash(fragmenthashPath);
						hashs4AllOriginalFragments.put(rootKey, hashValue);
						System.out.println(rootKey+":"+hashValue);
						count++;
					}
				}
			}
		} else {
			System.out.println("searchPath is null");
		}
		System.out.println(count);
		return hashs4AllOriginalFragments;
	}
	
	public static void testSearch(){
		DecimalFormat df = new DecimalFormat("#.00000");
		TreeMap<String,String> multiSimilarList = new TreeMap<String,String>();
		double similar = 0.0;
		double syntaxSimilar = 0.0;
		double syntaxSimilar4OriginalCode = 0.0;
		double semanticSimilar = 0.0;
		double syntaxWeight = 0.7;
		double semanticWeight = 0.3;
		String sourcePath = "D:/data/jhotdraw/65/fragmenthash/fragmenthash.txt";
		String rootPath = "D:/data/jhotdraw/";
		String sourceBasePath = "D:/data/jhotdraw/";
		String resultPath = "D:/data";
		int sourceVersion = 65;
		int ver = 65;
		int tarVer = 101;
		String originalCodeSourcePath = "D:/data/jhotdraw/65/originalcode/fragmenthash/fragmenthash.txt";
		syntaxSimilar = FragmentHashSearchEngine.compareSyntaxSimilar4Fragments(sourcePath,rootPath,tarVer);	
		semanticSimilar = SemanticSimilar4Identifier.semanticSimilar4FragmentIdentifier(sourceBasePath,sourceVersion,rootPath,tarVer);

		File file1=new File(originalCodeSourcePath);
		File file2=new File(rootPath+tarVer+"/originalcode/fragmenthash/fragmenthash.txt");
		if(file1.exists() && file2.exists()){
			syntaxSimilar4OriginalCode = FragmentHashSearchEngine.compareSyntaxSimilar4OriginalCodeFragments(originalCodeSourcePath,rootPath,tarVer);
		}
	
		
		if(syntaxSimilar!=0 && syntaxSimilar4OriginalCode==0){
			similar = (syntaxSimilar*syntaxWeight)+(semanticSimilar*semanticWeight);
			multiSimilarList.put(rootPath+"&"+ver, df.format(similar)+"&"+df.format(syntaxSimilar)+"&"+df.format(semanticSimilar));
			//similarList.put(rootPath+"&"+ver, Double.parseDouble(df.format(similar)));
			System.out.println(rootPath+"->"+ver+":TotalSimilarity:"+df.format(similar)+"   SyntaxSimilarity:"+df.format(syntaxSimilar)+"   SemanticSimilarity:"+df.format(semanticSimilar));
		} else if(syntaxSimilar!=0 && syntaxSimilar4OriginalCode!=0){
			similar = (syntaxSimilar*syntaxWeight)+(semanticSimilar*semanticWeight);
			multiSimilarList.put(rootPath+"&"+ver, df.format(similar)+"&"+df.format(syntaxSimilar)+"&"+df.format(semanticSimilar)+"&"+df.format(syntaxSimilar4OriginalCode));
			//similarList.put(rootPath+"&"+ver, Double.parseDouble(df.format(similar)));
			System.out.println(rootPath+"->"+ver+":TotalSimilarity:"+df.format(similar)+"   SyntaxSimilarity:"+df.format(syntaxSimilar)+"   SemanticSimilarity:"+df.format(semanticSimilar)+"   syntaxSimilar4OriginalCode:"+df.format(syntaxSimilar4OriginalCode));
		} else{
			System.out.println("null!");
		}
		FragmentHashSearchEngine.outputSimilar(resultPath, multiSimilarList, sourceBasePath,  sourceVersion);
	}
}
