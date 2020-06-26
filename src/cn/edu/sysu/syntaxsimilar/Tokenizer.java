package cn.edu.sysu.syntaxsimilar;
import org.eclipse.jdt.core.dom.*;

import cn.edu.sysu.util;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.Set;
import java.util.HashSet;

public class Tokenizer {
	
    public boolean inMethod = false;

    List<Integer> currentTokenList = new ArrayList<Integer>();

    String currentMethodName = "";
    Method currentMethodObj;

    int startLine;
    int endLine;

    int minNumLines;

    boolean statementHasMethodInvocation = false;
    boolean methodHasMethodInvocation = false;

    boolean debugStatements;

    ArrayList<Method> methodList = new ArrayList<Method>();
    
    HashSet<String> simpleNameList = new HashSet<String>();

    public Tokenizer(int numLines, boolean debug) {
        minNumLines = numLines;
        debugStatements = debug;
    }

    public void insertSimpleName(String name) {
        simpleNameList.add(name);
    }

    public void methodStart(String name, int mStartLine) {
        if (currentMethodName == "") {
            currentMethodName = name;
            currentMethodObj = new Method(mStartLine);
            methodHasMethodInvocation = false;

            if (debugStatements == true) {
                System.out.println("\n\n");
            }
        }
    }
    
    public void methodEnd(String name, int mEndLine) {
        // prevent methods within a method
    	if (currentMethodName.equals(name)) {
            currentMethodName = "";

            if (currentMethodObj.getNumStatements() >= minNumLines &&
                    methodHasMethodInvocation == true) {
                //currentMethodObj.buildHash(minNumLines);
                currentMethodObj.setEndLine(mEndLine);
                methodList.add(currentMethodObj);
            } else {
                // clear the statments
                currentTokenList.clear();
            }
        }
    }

    public void statementStart(int sLine, int eLine) {
        startLine = sLine;
        endLine = eLine;
        statementHasMethodInvocation = false;
        simpleNameList = new HashSet<String>();
    }

    public void statementEnd(int scopeLevel,int lineNum,String basePath, int verNum) {
        if (!currentTokenList.isEmpty()) {
            int hash_value = hashLine(currentTokenList, lineNum, basePath,  verNum);
            currentTokenList.clear();

            if (inMethod == true) {
                currentMethodObj.addStatement(hash_value, startLine, endLine,
                        statementHasMethodInvocation, scopeLevel, simpleNameList);

                // debug
/*                if (debugStatements == true) {
                    System.out.println("added");
                    System.out.print("terms : ");
                    for (String simpleName : simpleNameList) {
                        System.out.print(simpleName + " ");
                    }
                    System.out.println("");
                }*/
            }
        }
    }
    
    public void statementEnd2(int scopeLevel,int lineNum,String basePath, int verNum) {
        if (!currentTokenList.isEmpty()) {
            int hash_value = hashLine2(currentTokenList, lineNum, basePath,  verNum);
            currentTokenList.clear();

            if (inMethod == true) {
                currentMethodObj.addStatement(hash_value, startLine, endLine,
                        statementHasMethodInvocation, scopeLevel, simpleNameList);

                // debug
/*                if (debugStatements == true) {
                    System.out.println("added");
                    System.out.print("terms : ");
                    for (String simpleName : simpleNameList) {
                        System.out.print(simpleName + " ");
                    }
                    System.out.println("");
                }*/
            }
        }
    }
    
    public void getHash(int nodeType, String str) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((str == null) ? 0 : str.hashCode());
        result = prime * result + nodeType;
        currentTokenList.add(result);
    }
    
    public int hashLine(List<Integer> statementTokens,int lineNum,String basePath, int verNum){
    	
		    final int prime = 31;
		    int result = 1;   	
    	
		    for (Integer tkn : statementTokens) {
    		    result = prime * result + tkn;
    	  }
		
        if (debugStatements == true) {
            //System.out.printf("\t>> Hashed statement: %d\n", result);
            
            File dir1 = new File(basePath+"/"  + verNum + "/hash/");
    		if (!dir1.exists()) {
				dir1.mkdirs();
			}
			File file = new File(dir1.getPath() + "/hash.txt");
			//firstly clear hash.txt ;
			//util.clearTxtFile(file.getAbsolutePath());
			try {
				 FileOutputStream fos = new FileOutputStream(file,true);
			     OutputStreamWriter osw = new OutputStreamWriter(fos);
			     BufferedWriter bw = new BufferedWriter(osw);			   				
			     bw.write(lineNum+":"+result+"\r\n");
			     bw.flush();
			     bw.close();
			     osw.close();
			     fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

		    return result;
    }
    
    public int hashLine2(List<Integer> statementTokens,int lineNum,String basePath, int verNum){
    	
	    final int prime = 31;
	    int result = 1;   	
	
	    for (Integer tkn : statementTokens) {
		    result = prime * result + tkn;
	  }
	
    if (debugStatements == true) {
        //System.out.printf("\t>> Hashed statement: %d\n", result);
        
        File dir1 = new File(basePath+"/"  + verNum + "/originalcode/hash/");
		if (!dir1.exists()) {
			dir1.mkdirs();
		}
		File file = new File(dir1.getPath() + "/hash.txt");
		//firstly clear hash.txt ;
		//util.clearTxtFile(file.getAbsolutePath());
		try {
			 FileOutputStream fos = new FileOutputStream(file,true);
		     OutputStreamWriter osw = new OutputStreamWriter(fos);
		     BufferedWriter bw = new BufferedWriter(osw);			   				
		     bw.write(lineNum+":"+result+"\r\n");
		     bw.flush();
		     bw.close();
		     osw.close();
		     fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	    return result;
}

    public void hasMethodInvocation() {
        statementHasMethodInvocation = true;
        methodHasMethodInvocation = true;
    }

    // Add node name
    public void addHash(int type, int lineNum,String basePath, int verNum)  {
        getHash(type, null);
        if (debugStatements == true) {
            debug(type, null, lineNum,basePath,verNum);
        }
    }
    
    public void addHash2(int type, int lineNum,String basePath, int verNum)  {
        getHash(type, null);
        if (debugStatements == true) {
            debug2(type, null, lineNum,basePath,verNum);
        }
    }

    // Add code element
    public void addHash(int type, String str, int lineNum,String basePath, int verNum) {        
        getHash(type, str);
        if (debugStatements == true) {
            debug(type, str, lineNum,basePath,verNum);
        }
    }
    
    public void addHash2(int type, String str, int lineNum,String basePath, int verNum) {        
        getHash(type, str);
        if (debugStatements == true) {
            debug2(type, str, lineNum,basePath,verNum);
        }
    }

    private void debug(int type, String str, int lineNum, String basePath, int verNum) {
    
    	if (str == null) {
    		//System.out.printf("	>> \"%s\" at line \"%d\"\n", TokenType.values()[type].toString(), lineNum);
    		
    		File dir1 = new File(basePath+"/"  + verNum + "/tokenizer");
    		if (!dir1.exists()) {
				dir1.mkdirs();
			}
			File file = new File(dir1.getPath() + "/token.txt");
			//firstly clear token.txt ;
			//util.clearTxtFile(file.getAbsolutePath());
			try {
				 FileOutputStream fos = new FileOutputStream(file,true);
			     OutputStreamWriter osw = new OutputStreamWriter(fos);
			     BufferedWriter bw = new BufferedWriter(osw);			   				
			     bw.write(lineNum+":"+TokenType.values()[type].toString()+"\r\n");
			     bw.flush();
			     bw.close();
			     osw.close();
			     fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else {
            str = str.replaceAll("\\n","");
    		//System.out.printf("	>> \"%s\" with value \"%s\" at line \"%d\"\n", TokenType.values()[type].toString(), str, lineNum);
    		
    		File dir1 = new File(basePath+"/"  + verNum + "/identifier");
    		if (!dir1.exists()) {
				dir1.mkdirs();
			}
			File file = new File(dir1.getPath() + "/identifier.txt");
			try {
				 FileOutputStream fos = new FileOutputStream(file,true);
			     OutputStreamWriter osw = new OutputStreamWriter(fos);
			     BufferedWriter bw = new BufferedWriter(osw);			   				
			     bw.write(lineNum+":"+str+":"+TokenType.values()[type].toString()+"\r\n");
			     bw.flush();
			     bw.close();
			     osw.close();
			     fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	
    }
    
    private void debug2(int type, String str, int lineNum, String basePath, int verNum) {
        
    	if (str == null) {
    		//System.out.printf("	>> \"%s\" at line \"%d\"\n", TokenType.values()[type].toString(), lineNum);
    		
    		File dir1 = new File(basePath+"/"  + verNum + "/originalcode/tokenizer");
    		if (!dir1.exists()) {
				dir1.mkdirs();
			}
			File file = new File(dir1.getPath() + "/token.txt");
			//firstly clear token.txt ;
			//util.clearTxtFile(file.getAbsolutePath());
			try {
				 FileOutputStream fos = new FileOutputStream(file,true);
			     OutputStreamWriter osw = new OutputStreamWriter(fos);
			     BufferedWriter bw = new BufferedWriter(osw);			   				
			     bw.write(lineNum+":"+TokenType.values()[type].toString()+"\r\n");
			     bw.flush();
			     bw.close();
			     osw.close();
			     fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else {
            str = str.replaceAll("\\n","");
    		//System.out.printf("	>> \"%s\" with value \"%s\" at line \"%d\"\n", TokenType.values()[type].toString(), str, lineNum);
    		
    		File dir1 = new File(basePath+"/"  + verNum + "/originalcode/identifier");
    		if (!dir1.exists()) {
				dir1.mkdirs();
			}
			File file = new File(dir1.getPath() + "/identifier.txt");
			try {
				 FileOutputStream fos = new FileOutputStream(file,true);
			     OutputStreamWriter osw = new OutputStreamWriter(fos);
			     BufferedWriter bw = new BufferedWriter(osw);			   				
			     bw.write(lineNum+":"+str+":"+TokenType.values()[type].toString()+"\r\n");
			     bw.flush();
			     bw.close();
			     osw.close();
			     fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	
    }

    public ArrayList<Method> getTokenizedMethods() {
        return methodList;   
    }
}

