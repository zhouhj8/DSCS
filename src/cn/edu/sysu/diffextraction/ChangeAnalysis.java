package cn.edu.sysu.diffextraction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.FileUtils;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;


public class ChangeAnalysis {
	public static String loc = "../Datasets/";
	public static String pro = "SWT";
	public static String version = "3.1";
	
	
	public static void changeDistill(String file1, String file2, int verNum, String basePath) throws Exception {
		File left = new File(file1);
		File right = new File(file2);
		if(!left.exists() || !right.exists()){
			System.err.println("File do not exists:");
		}
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.out.println("Warning: error while change distilling. " + e.getMessage());
		    System.out.println(file1);
		    System.out.println(file2);
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
//		System.out.println(changes.size());
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		        // see Javadocs for more information
		    	int start = change.getChangedEntity().getStartPosition();
		    	int end = change.getChangedEntity().getEndPosition();
		    	int startLine = 0, endLine = 0;
		    	
		    	if (change instanceof Insert) {
		    		startLine = getLineNumber(file2, start);
		    		endLine = getLineNumber(file2, end);
		    	} else if (change instanceof Update || change instanceof Delete || change instanceof Move) {
		    		startLine = getLineNumber(file1, start);
		    		endLine = getLineNumber(file1,end);
		    	} else {
		    		System.out.println("Error no type");
		    	}
		    	ChangeType ct = change.getChangeType();
		    	//System.out.println(ct.name() + "\t" + startLine + "\t" + endLine);
		    	
		    	
	            File dir1 = new File(basePath+"/"  + verNum + "/changedistill/");
	    		if (!dir1.exists()) {
					dir1.mkdirs();
				}
				File file = new File(dir1.getPath() + "/changedistill.txt");
				try {
					 FileOutputStream fos = new FileOutputStream(file,true);
				     OutputStreamWriter osw = new OutputStreamWriter(fos);
				     BufferedWriter bw = new BufferedWriter(osw);			   				
				     bw.write(ct.name() + ":" + startLine + ":" + endLine+"\n");
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
	}
	
	public static void changeDistill4OriginalCode(String file1, String file2, int verNum, String basePath) throws Exception {
		File left = new File(file1);
		File right = new File(file2);
		if(!left.exists() || !right.exists()){
			System.err.println("File do not exists:");
		}
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.out.println("Warning: error while change distilling. " + e.getMessage());
		    System.out.println(file1);
		    System.out.println(file2);
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
//		System.out.println(changes.size());
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		        // see Javadocs for more information
		    	int start = change.getChangedEntity().getStartPosition();
		    	int end = change.getChangedEntity().getEndPosition();
		    	int startLine = 0, endLine = 0;
		    	
		    	if (change instanceof Insert) {
		    		startLine = getLineNumber(file2, start);
		    		endLine = getLineNumber(file2, end);
		    	} else if (change instanceof Update || change instanceof Delete || change instanceof Move) {
		    		startLine = getLineNumber(file1, start);
		    		endLine = getLineNumber(file1,end);
		    	} else {
		    		System.out.println("Error no type");
		    	}
		    	ChangeType ct = change.getChangeType();
		    	//System.out.println(ct.name() + "\t" + startLine + "\t" + endLine);
		    	
		    	
	            File dir1 = new File(basePath+"/"  + verNum + "/originalcode/changedistill/");
	    		if (!dir1.exists()) {
					dir1.mkdirs();
				}
				File file = new File(dir1.getPath() + "/changedistill.txt");
				try {
					 FileOutputStream fos = new FileOutputStream(file,true);
				     OutputStreamWriter osw = new OutputStreamWriter(fos);
				     BufferedWriter bw = new BufferedWriter(osw);			   				
				     bw.write(ct.name() + ":" + startLine + ":" + endLine+"\n");
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
	}

	public static int getLineNumber(String file, int position) throws Exception{
		int lineNum = 1;
		String fileContent = FileUtils.getContent(new File(file));
		char[] charArray = fileContent.toCharArray();
		for(int i=0; i<position&&i<charArray.length; i++){
			if(charArray[i]=='\n'){
				lineNum++;
			}
		}		
		return lineNum;
	}
	
	public static void analysisChange(int verNum, String basePath) throws Exception{
		String prefix = loc + pro + File.separator + pro + "_" + version + File.separator;
		String saveFile = prefix + "hashPairs.txt";
		List<String> lines = FileToLines.fileToLines(saveFile);
		int count = 0;
		for (String line : lines) {
			count++;
//			if (count > 1000) break;
			String[] splits = line.split("\t");
			String hash1 = splits[0];
			String hash2 = splits[1];
//			System.out.println(hash1 + "\t" + hash2);
			
			
			String file1 = prefix + "changes" + File.separator + hash1 + ".java";
			String file2 = prefix + "changes" + File.separator + hash2 + ".java";
			System.out.println(hash1 + "\t" + hash2 + "\t");
			changeDistill(file1, file2, verNum, basePath);
			
		}
	}

	public static void main(String[] args) throws Exception {
		analysisChange(103,"D:/log/");
		changeDistill("D:/log/103/old/SAXParserImpl.java","D:/log/103/new/SAXParserImpl.java",103,"D:/log/");
		
	}
	
	
}
