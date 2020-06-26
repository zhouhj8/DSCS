package cn.edu.sysu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.hashgeneration.CenerateCodeFragmentHash;
import cn.edu.sysu.syntaxsimilar.Parser;
import cn.edu.sysu.syntaxsimilar.Tokenizer;

public class TokenizerMainClass {

	public static void main(String[] agrs) {
		/**
		 * tokenizer (generate hash code) for a complete java file---Parser.parseAST2Tokens();
		 * extract changed code lines---ChangeAnalysis.changeDistill();
		 * extract hash code for changed code lines---util.extractChangedCodeFragment.
		 */
		
		CenerateCodeFragmentHash.generateFragmentHash();
		
		/**syntax similarity computing
		 * 
		 */
		
	}
	
	
}
