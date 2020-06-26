package cn.edu.sysu.syntaxSimilar4originalcode;

import java.util.ArrayList;

import cn.edu.sysu.util;
import cn.edu.sysu.config.ConfigOperation;

/**
 * 
 * @author Troy
 *
 */
public class TypeChecking4Statements {
	
	public static boolean typeChecking4ChangedStatements(String basePath, int ver){
		String[] types = null;
		ArrayList<String> changeTypes = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		types = ConfigOperation.getConfigProperties("changeTypes").split("&");
		for(int i = 0; i<types.length; i++){
			changeTypes.add(types[i]);
		}
		result = util.readChangeTypesinDistillFile(basePath, ver);
		if(result == null){
			return false;
		}
		result.removeAll(changeTypes);
		 if(result.size() == 0){
			 return true;
		 }else{
			 return false;
		 }
	}
	
	

}
