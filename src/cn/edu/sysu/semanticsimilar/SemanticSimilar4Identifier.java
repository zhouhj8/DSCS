package cn.edu.sysu.semanticsimilar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.edu.sysu.util;
import cn.edu.sysu.config.ConfigOperation;

public class SemanticSimilar4Identifier {
	
	public static void main(String[] args){
		double similar = semanticSimilar4FragmentIdentifier("D:/data/jhotdraw/",241,"D:/data/jedit/",24067);
		System.out.println(similar);
	}
	
	public static double semanticSimilar4FragmentIdentifier(String sourceBasePath,int sourceVersion,String basePath, int ver2){
		ArrayList<String> sourceIdentifier = new ArrayList<String>();
		ArrayList<String> tagartIdentifier = new ArrayList<String>();
		
		String filterWords = ConfigOperation.getConfigProperties("filterWords");
		String[] filterWordsArray = filterWords.split("&");
		ArrayList<String> filterWordsList = new ArrayList<String>(Arrays.asList(filterWordsArray));
		
		sourceIdentifier = util.extractChangedCodeIdentifier1(sourceBasePath, sourceVersion);
		tagartIdentifier = util.extractChangedCodeIdentifier1(basePath, ver2);
		
		sourceIdentifier.removeAll(filterWordsList);
		tagartIdentifier.removeAll(filterWordsList);
				
		ArrayList<String> sourceIdentifierCopy = new ArrayList<String>();
		for(int i=0;i<sourceIdentifier.size();i++){
			sourceIdentifierCopy.add(sourceIdentifier.get(i));
		}
		ArrayList<String> tagartIdentifierCopy = new ArrayList<String>();
		for(int j=0;j<tagartIdentifier.size();j++){
			tagartIdentifierCopy.add(tagartIdentifier.get(j));
		}
						
		ArrayList<String> sourceIdentifierCopy2 = new ArrayList<String>();
		for(int i=0;i<sourceIdentifier.size();i++){
			sourceIdentifierCopy2.add(sourceIdentifier.get(i));
		}
		ArrayList<String> tagartIdentifierCopy2 = new ArrayList<String>();
		for(int j=0;j<tagartIdentifier.size();j++){
			tagartIdentifierCopy2.add(tagartIdentifier.get(j));
		}
		
		sourceIdentifier.removeAll(tagartIdentifier);
		tagartIdentifierCopy.removeAll(sourceIdentifierCopy);
		
		double sourceIdentifierSize = sourceIdentifier.size();
		double tagartIdentifierSize = tagartIdentifierCopy.size();
		
		sourceIdentifierCopy2.retainAll(tagartIdentifierCopy2);
		double intersectionSize = sourceIdentifierCopy2.size();
		
        if((sourceIdentifierSize + tagartIdentifierSize+intersectionSize) !=0){
        	return ((intersectionSize)/(sourceIdentifierSize + tagartIdentifierSize+intersectionSize)); 
        }else{
        	return 0;
        }
		
		
	}
	
	public static double semanticSimilar4FragmentIdentifier2(ArrayList<String> sourceIdentifier, ArrayList<String> tagartIdentifier){
		String filterWords = ConfigOperation.getConfigProperties("filterWords");
		String[] filterWordsArray = filterWords.split("&");
		ArrayList<String> filterWordsList = new ArrayList<String>(Arrays.asList(filterWordsArray));
		sourceIdentifier.removeAll(filterWordsList);

				
		ArrayList<String> sourceIdentifierCopy = new ArrayList<String>();
		for(int i=0;i<sourceIdentifier.size();i++){
			sourceIdentifierCopy.add(sourceIdentifier.get(i));
		}
		ArrayList<String> tagartIdentifierCopy = new ArrayList<String>();
		for(int j=0;j<tagartIdentifier.size();j++){
			tagartIdentifierCopy.add(tagartIdentifier.get(j));
		}
		
		ArrayList<String> sourceIdentifierCopy1 = new ArrayList<String>();
		for(int i=0;i<sourceIdentifier.size();i++){
			sourceIdentifierCopy1.add(sourceIdentifier.get(i));
		}
		ArrayList<String> tagartIdentifierCopy1 = new ArrayList<String>();
		for(int j=0;j<tagartIdentifier.size();j++){
			tagartIdentifierCopy1.add(tagartIdentifier.get(j));
		}
						
		ArrayList<String> sourceIdentifierCopy2 = new ArrayList<String>();
		for(int i=0;i<sourceIdentifier.size();i++){
			sourceIdentifierCopy2.add(sourceIdentifier.get(i));
		}
		ArrayList<String> tagartIdentifierCopy2 = new ArrayList<String>();
		for(int j=0;j<tagartIdentifier.size();j++){
			tagartIdentifierCopy2.add(tagartIdentifier.get(j));
		}
		
		sourceIdentifierCopy1.removeAll(tagartIdentifierCopy1);
		tagartIdentifierCopy.removeAll(sourceIdentifierCopy);
		
		double sourceIdentifierSize = sourceIdentifierCopy1.size();
		double tagartIdentifierSize = tagartIdentifierCopy.size();
		
		sourceIdentifierCopy2.retainAll(tagartIdentifierCopy2);
		double intersectionSize = sourceIdentifierCopy2.size();
		
        if((sourceIdentifierSize + tagartIdentifierSize+intersectionSize) !=0){
        	return ((intersectionSize)/(sourceIdentifierSize + tagartIdentifierSize+intersectionSize)); 
        }else{
        	return 0;
        }
		
		
	}

}
