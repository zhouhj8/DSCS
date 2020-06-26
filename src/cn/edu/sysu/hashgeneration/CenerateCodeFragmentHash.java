package cn.edu.sysu.hashgeneration;

import java.io.File;
import java.util.ArrayList;

import cn.edu.sysu.util;
import cn.edu.sysu.config.ConfigOperation;
import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.syntaxSimilar4originalcode.TypeChecking4Statements;
import cn.edu.sysu.syntaxsimilar.Parser;
import cn.edu.sysu.syntaxsimilar.Tokenizer;

public class CenerateCodeFragmentHash {

	/**
	 * tokenizer (generate hash code) for a complete java
	 * file---Parser.parseAST2Tokens(); extract changed code
	 * lines---ChangeAnalysis.changeDistill(); extract hash code for changed
	 * code lines---util.extractChangedCodeFragment.
	 */
	public static void generateFragmentHash() {		
		String rootPath = ConfigOperation.getConfigProperties("basePath25");
		for (int ver = 1; ver < 23415; ver++) {			
			String basePath1 = rootPath + "/" + ver + "/new/src/";
			String basePath2 = rootPath + "/" + ver + "/old/src/";

			File f1 = new File(basePath1);
			File f2 = new File(basePath2);
			if (!f1.exists() || !f2.exists()) {
				continue;
			}
			System.out.println("version number--------------------------------------------------:" + ver);

			if ((util.countJavaFiles(basePath1) == 1) && (util.countJavaFiles(basePath2) == 1)) {
				// tokenizer
				String fileName = util.obtainClassName(basePath1);
				String className = fileName;
				System.out.println("className:" + fileName);
				String absolutePathNew = basePath1 + className;
				System.out.println("absolutePathNew:" + absolutePathNew);
				String absolutePathOld = basePath2 + className;
				ArrayList<String> fileProcessError = new ArrayList<String>();

				File dir0 = new File(rootPath + "/" + ver + "/identifier");
				if (!dir0.exists()) {
					dir0.mkdirs();
				}
				File file0 = new File(dir0.getPath() + "/identifier.txt");
				// firstly clear identifier.txt ;
				util.clearTxtFile(file0.getAbsolutePath());

				File dir1 = new File(rootPath + "/" + ver + "/tokenizer");
				if (!dir1.exists()) {
					dir1.mkdirs();
				}
				File file1 = new File(dir1.getPath() + "/token.txt");
				// firstly clear token.txt ;
				util.clearTxtFile(file1.getAbsolutePath());

				File dir2 = new File(rootPath + "/" + ver + "/hash/");
				if (!dir2.exists()) {
					dir2.mkdirs();
				}
				File file2 = new File(dir2.getPath() + "/hash.txt");
				// firstly clear hash.txt ;
				util.clearTxtFile(file2.getAbsolutePath());

				Tokenizer token = new Tokenizer(2, true);
				token = Parser.parseAST2Tokens(absolutePathNew, 2, true, rootPath, ver);
				if (token == null) {
					// error at parsing the token list, abort
					fileProcessError.add(absolutePathNew);
					System.out.println("token == null" + fileProcessError.size());
				}

				// code diff checking, return the number of changed code lines;
				try {
					File dir3 = new File(rootPath + "/" + ver + "/changedistill/");
					if (!dir3.exists()) {
						dir3.mkdirs();
					}
					File file3 = new File(dir3.getPath() + "/changedistill.txt");
					// firstly clear changedistill.txt ;
					util.clearTxtFile(file3.getAbsolutePath());

					ChangeAnalysis.changeDistill(absolutePathOld, absolutePathNew, ver, rootPath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				File dir4 = new File(rootPath + "/" + ver + "/fragmenthash");
				if (!dir4.exists()) {
					dir4.mkdirs();
				}
				File file4 = new File(dir4.getPath() + "/fragmenthash.txt");
				// firstly clear fragmenthash.txt ;
				util.clearTxtFile(file4.getAbsolutePath());

				// extract hash code for changed code lines
				util.extractChangedCodeFragment(rootPath, ver);

				//if the code fragment in a class only involves changed statements, generate hash code for the corresponding original statements(in old version)
				if (TypeChecking4Statements.typeChecking4ChangedStatements(rootPath, ver)) {
					File dir10 = new File(rootPath + "/" + ver + "/originalcode/identifier/");
					if (!dir10.exists()) {
						dir10.mkdirs();
					}
					File file10 = new File(dir10.getPath() + "/identifier.txt");
					// firstly clear identifier.txt ;
					util.clearTxtFile(file10.getAbsolutePath());

					File dir11 = new File(rootPath + "/" + ver + "/originalcode/tokenizer");
					if (!dir11.exists()) {
						dir11.mkdirs();
					}
					File file11 = new File(dir11.getPath() + "/token.txt");
					// firstly clear token.txt ;
					util.clearTxtFile(file11.getAbsolutePath());

					File dir12 = new File(rootPath + "/" + ver + "/originalcode/hash/");
					if (!dir12.exists()) {
						dir12.mkdirs();
					}
					File file12 = new File(dir12.getPath() + "/hash.txt");
					// firstly clear hash.txt ;
					util.clearTxtFile(file12.getAbsolutePath());

					Tokenizer token1 = new Tokenizer(2, true);
					token1 = Parser.parseAST2Tokens2(absolutePathOld, 2, true, rootPath, ver);
					if (token1 == null) {
						// error at parsing the token list, abort
						fileProcessError.add(absolutePathOld);
						System.out.println("token == null" + fileProcessError.size());
					}

					// code diff checking, return the number of changed code
					// lines;
					try {
						File dir13 = new File(rootPath + "/" + ver + "/originalcode/changedistill/");
						if (!dir13.exists()) {
							dir13.mkdirs();
						}
						File file13 = new File(dir13.getPath() + "/changedistill.txt");
						// firstly clear changedistill.txt ;
						util.clearTxtFile(file13.getAbsolutePath());

						ChangeAnalysis.changeDistill4OriginalCode(absolutePathNew, absolutePathOld, ver, rootPath);
					} catch (Exception e) {
						e.printStackTrace();
					}

					File dir14 = new File(rootPath + "/" + ver + "/originalcode/fragmenthash");
					if (!dir14.exists()) {
						dir14.mkdirs();
					}
					File file14 = new File(dir14.getPath() + "/fragmenthash.txt");
					// firstly clear fragmenthash.txt ;
					util.clearTxtFile(file14.getAbsolutePath());

					// extract hash code for changed code lines
					util.extractChangedCodeFragment4OriginalCode(rootPath, ver);
				}

			} else if ((util.countFiles(basePath1) == 0) && (util.countFiles(basePath2) == 1)) {
				System.out.println("It's a deleted class");
				continue;

			} else if ((util.countFiles(basePath1) == 1) && (util.countFiles(basePath2) == 0)) {
				System.out.println("It's a new class");
				continue;

			} else if ((util.countFiles(basePath1) == 0) && (util.countFiles(basePath2) == 0)) {
				System.out.println("It's no changes in this commit");
				continue;

			} else if ((util.countFiles(basePath1) > 1) && (util.countFiles(basePath2) > 1)) {
				System.out.println("mutiple classes changed in this commit");
				continue;
			}
		}
	}
	
	public static void generateToken4OriginalCode(){
		int count = 0;
		for (int ver = 1; ver < 1083; ver++) {
			String rootPath = ConfigOperation.getConfigProperties("basePath13");
			String basePath1 = rootPath + "/" + ver + "/new/src/";
			String basePath2 = rootPath + "/" + ver + "/old/src/";

			File f1 = new File(basePath1);
			File f2 = new File(basePath2);
			if (!f1.exists() || !f2.exists()) {
				continue;
			}
			
			if ((util.countJavaFiles(basePath1) == 1) && (util.countJavaFiles(basePath2) == 1)) {
				String fileName = util.obtainClassName(basePath1);
				String className = fileName;
				System.out.println("className:" + fileName);
				String absolutePathNew = basePath1 + className;
				System.out.println("absolutePathNew:" + absolutePathNew);
				String absolutePathOld = basePath2 + className;
				ArrayList<String> fileProcessError = new ArrayList<String>();
				//if the code fragment in a class only involves changed statements, generate hash code for the corresponding original statements(in old version)
				if (TypeChecking4Statements.typeChecking4ChangedStatements(rootPath, ver)) {
					count++;
					File dir10 = new File(rootPath + "/" + ver + "/originalcode/identifier/");
					if (!dir10.exists()) {
						dir10.mkdirs();
					}
					File file10 = new File(dir10.getPath() + "/identifier.txt");
					// firstly clear identifier.txt ;
					util.clearTxtFile(file10.getAbsolutePath());

					File dir11 = new File(rootPath + "/" + ver + "/originalcode/tokenizer");
					if (!dir11.exists()) {
						dir11.mkdirs();
					}
					File file11 = new File(dir11.getPath() + "/token.txt");
					// firstly clear token.txt ;
					util.clearTxtFile(file11.getAbsolutePath());

					File dir12 = new File(rootPath + "/" + ver + "/originalcode/hash/");
					if (!dir12.exists()) {
						dir12.mkdirs();
					}
					File file12 = new File(dir12.getPath() + "/hash.txt");
					// firstly clear hash.txt ;
					util.clearTxtFile(file12.getAbsolutePath());

					Tokenizer token1 = new Tokenizer(2, true);
					token1 = Parser.parseAST2Tokens2(absolutePathOld, 2, true, rootPath, ver);
					if (token1 == null) {
						// error at parsing the token list, abort
						fileProcessError.add(absolutePathOld);
						System.out.println("token == null" + fileProcessError.size());
					}

					// code diff checking, return the number of changed code
					// lines;
					try {
						File dir13 = new File(rootPath + "/" + ver + "/originalcode/changedistill/");
						if (!dir13.exists()) {
							dir13.mkdirs();
						}
						File file13 = new File(dir13.getPath() + "/changedistill.txt");
						// firstly clear changedistill.txt ;
						util.clearTxtFile(file13.getAbsolutePath());

						ChangeAnalysis.changeDistill4OriginalCode(absolutePathNew, absolutePathOld, ver, rootPath);
					} catch (Exception e) {
						e.printStackTrace();
					}

					File dir14 = new File(rootPath + "/" + ver + "/originalcode/fragmenthash");
					if (!dir14.exists()) {
						dir14.mkdirs();
					}
					File file14 = new File(dir14.getPath() + "/fragmenthash.txt");
					// firstly clear fragmenthash.txt ;
					util.clearTxtFile(file14.getAbsolutePath());

					// extract hash code for changed code lines
					util.extractChangedCodeFragment4OriginalCode(rootPath, ver);
				}
			}
		}
			
		System.out.println("count:"+count);	
	}
/*	public static void main(String[] args){
		//generateToken4OriginalCode();
		//generateFragmentHash();
	}*/

}
