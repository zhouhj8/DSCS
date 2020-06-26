package cn.edu.sysu.SVNDigger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.SVNDiffCallback;
import org.tmatesoft.svn.core.internal.wc.SVNRemoteDiffEditor;
import org.tmatesoft.svn.core.io.ISVNDeltaConsumer;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindowApplyBaton;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;



public class SVNHelper {

	static {
		DAVRepositoryFactory.setup();
	}

	private SVNHelper() {

	}
	
	public static void downloadClientDiffCode(String strPath, long verNum1, String url, long verNum2){
		
		File path1 = new File(strPath);
		
		Collection<java.lang.String> changeLists = null;
		SVNURL url2 = null;
		try {
			url2 = SVNURL.parseURIEncoded(url);
		} catch (SVNException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SVNRevision rN = SVNRevision.create(verNum1);
		SVNRevision rM = SVNRevision.create(verNum2);
		
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		SVNDiffClient diffClient = new SVNDiffClient(authManager, options);
		
		ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
		try {
			

			changeLists = new LinkedList<String>();
			
			diffClient.doDiff(path1, rN, url2, rM, SVNDepth.INFINITY, false, outputStream4, changeLists);
			
			BufferedWriter output4 = new BufferedWriter(new FileWriter("D:/diff.txt"));
			output4.write(outputStream4.toString());
			outputStream4.close();
			output4.close();
		} catch (IOException | SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String change : changeLists){
			System.out.println(change);
		}
	}
	
	public static void downloadSVNDiffWindow(String path) throws SVNException{
		byte [] target = null;
		int targetLength = 100;
		FileInputStream fileInputStream;
		InputStreamReader inputStreamReader;
		BufferedReader bufferedReader;
		File file = new File(path);
		try {
			 fileInputStream =new FileInputStream(file);
			 inputStreamReader = new InputStreamReader(fileInputStream);
			 bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while((str = bufferedReader.readLine())!=null){
				target=str.getBytes();				
			}
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
			
		} catch (IOException e) {				
			e.printStackTrace();
		}

		
		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		deltaGenerator.sendDelta(path, target, targetLength, new ISVNDeltaConsumer(){
			@Override
			public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void textDeltaEnd(String path) throws SVNException {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void applyTextDelta(String path, String baseChecksum) throws SVNException {
				// TODO Auto-generated method stub
				path = "D:/diff/delta.txt";
				File file = new File(path);
				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();				
					BufferedWriter output = new BufferedWriter(new FileWriter(path));
					output.write(outputStream.toString());
					System.out.println(outputStream.toString());
					outputStream.close();
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}});
		
	}
	
	
    public static void downloadClientDiffCode2(String url1, long verNum1, String url2, long verNum2){

		SVNURL surl1 = null;
		SVNURL surl2 = null;
		BufferedWriter output4 = null;
		try {
			surl1 = SVNURL.parseURIEncoded(url1);
			surl2 = SVNURL.parseURIEncoded(url2);
		} catch (SVNException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SVNRevision rN = SVNRevision.create(verNum1);
		SVNRevision rM = SVNRevision.create(verNum2);
		
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		SVNDiffClient diffClient = new SVNDiffClient(authManager, options);
		
		ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
		try {						
			diffClient.doDiff(surl1, rN, surl2, rM, SVNDepth.IMMEDIATES, true, outputStream4);			
			output4 = new BufferedWriter(new FileWriter("D:/diff/diff.txt"));
			output4.write(outputStream4.toString());
			System.out.println(outputStream4.toString());
			outputStream4.close();
			output4.close();

		} catch (IOException | SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void downloadDiffCode( SVNURL SVNurl, long targetRevision, long revision, OutputStream result) throws SVNException, IOException{
		//SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		
		SVNRepository repository = SVNRepositoryFactory.create(SVNurl);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);
		SVNRemoteDiffEditor editor = null;	
		 try {
			
		ISVNReporterBaton iSVNReporterBaton = new ISVNReporterBaton() {
            public void report(ISVNReporter reporter) throws SVNException {
                //TODO(sd): dynamic depth here
                reporter.setPath("", null, targetRevision, SVNDepth.INFINITY, false);
                reporter.finishReport();
            }
        };
        
        SVNDiffCallback callback = new SVNDiffCallback(null, null, targetRevision, revision, result);
        
        callback.setBasePath(new File("D:/diff.txt"));        
        editor = new SVNRemoteDiffEditor(null, null, callback, repository, targetRevision, revision, false, null, null);
        editor.setUseGlobalTmp(true);
//		ISVNEditor iSVNEditor = new SVNEditor();
		
		repository.diff(SVNurl, targetRevision, revision, "", true, false, true, iSVNReporterBaton, editor);
		 } finally {
	            if (editor != null) {
	                editor.cleanup();
	            }
	            repository.closeSession();
	        }
		
	}
	


	


	/**
	 * 
	 * @param url
	 *            要下载的版本的url地址
	 * @param revisionNum
	 *            要下载的版本的版本号
	 * @param savedPath
	 *            下载文件的保存路径
	 * @throws SVNException
	 * @throws IOException
	 */
	public static void download(String url, int revisionNum, String savedPath) throws SVNException, IOException {
		SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(repositoryBaseUrl);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);

		Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "/" }, logEntries, revisionNum, revisionNum, true, true);

		for (SVNLogEntry logEntry : logEntries) {

			File dir1 = new File(savedPath+"/"  + revisionNum + "/new/src");// 当前版本文件的存储路径
			File dir2 = new File(savedPath+"/"  + revisionNum + "/old/src");// 前一个版本文件的存储路径
			File dir3 = new File(savedPath+"/"  + revisionNum + "/files");//结果文件的存储路径
			if (!dir1.exists()) {
				dir1.mkdirs();
			}
			if (!dir2.exists()) {
				dir2.mkdirs();
			}
			if(!dir3.exists()){
				dir3.mkdirs();
			}
			File logFile = new File(dir1.getPath() + "/" + logEntry.getRevision() + ".txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(logFile));

			output.write("---------------------------------------------\r\n");

			// 获取修改版本号

			output.write("修订版本号: " + logEntry.getRevision() + "\r\n");

			// 获取提交者

			output.write("提交者: " + logEntry.getAuthor() + "\r\n");

			// 获取提交时间

			output.write("日期: " + logEntry.getDate() + "\r\n");

			// 获取注释信息

			output.write("注释信息: " + logEntry.getMessage() + "\r\n");
			
			//output.write("Revision Properties: " + logEntry. + "\r\n");

			if (logEntry.getChangedPaths().size() > 0) {

				output.write("受影响的文件、目录:\r\n");

				for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {

					output.write(entry.getValue() + "\r\n");

					// 此变量用来存放要查看的文件的属性名/属性值列表。
					SVNProperties fileProperties = new SVNProperties();

					String temp = entry.getValue().toString();
					String filePath = "";// 文件的下载路径
					if (temp.indexOf("(") > 0) {
						filePath = temp.substring(temp.indexOf("/"), temp.indexOf("("));
					} else {
						filePath = temp.substring(temp.indexOf("/"));
					}

					String[] temps = filePath.split("/");
					String srcname = temps[temps.length - 1];// 获得文件名，不含路径

					// 只提取java文件
					if (srcname.endsWith(".java")) {
						if (repository.checkPath(filePath, logEntry.getRevision()) != SVNNodeKind.NONE) {
							// 此输出流用来存放要查看的文件的内容。
							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();// 当前版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision(), fileProperties, outputStream1);
							BufferedWriter output1 = new BufferedWriter(new FileWriter(dir1 + "/" + srcname));
							output1.write(outputStream1.toString());
							outputStream1.close();

							output1.close();
						}

						if (logEntry.getRevision() > 0
								&& repository.checkPath(filePath, logEntry.getRevision() - 1) != SVNNodeKind.NONE) {
							ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();// 前一个版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision() - 1, fileProperties, outputStream2);
							BufferedWriter output2 = new BufferedWriter(new FileWriter(dir2 + "/" + srcname));
							output2.write(outputStream2.toString());
							outputStream2.close();
							output2.close();

						}
					}
				}

			}
			output.close();

		}

	}

	/**
	 * 输出当前项目的SVN记录信息
	 * 
	 * @param url
	 * @param infoFilePath
	 * @throws SVNException
	 * @throws IOException
	 */
	public static void writeInfo(String url, String infoFilePath) throws SVNException, IOException {
		SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		// "http://svn.code.sf.net/p/java-game-lib/code/trunk"
		SVNRepository repository = SVNRepositoryFactory.create(repositoryBaseUrl);
		System.out.println("create repository.");
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);

		Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "/" }, logEntries, 0, -1, true, true);
		File logFile = new File(infoFilePath);

		BufferedWriter output = new BufferedWriter(new FileWriter(logFile));
		for (SVNLogEntry logEntry : logEntries) {

			System.out.println("---------------------------------------------");
			output.write("***\r\n");

			// 获取修改版本号

			System.out.println("修订版本号: " + logEntry.getRevision());
			output.write(logEntry.getRevision() + "\r\n");

			// 获取提交者

			System.out.println("提交者: " + logEntry.getAuthor());
			output.write(logEntry.getAuthor() + "\r\n");

			// 获取提交时间

			System.out.println("日期: " + logEntry.getDate());
			output.write(logEntry.getDate() + "\r\n");

			// 获取注释信息

			System.out.println("注释信息: " + logEntry.getMessage());
			output.write(logEntry.getMessage() + "\r\n");

			if (logEntry.getChangedPaths().size() > 0) {

				System.out.println();
				for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {
					if (entry.getValue().toString().indexOf(".java") > 0) {
						System.out.println(entry.getValue());
						output.write(entry.getValue() + "\r\n");
					}
				}
			}
		}
		output.close();
	}
	
	public static void downloadJavaFile(String url, int revisionNum, String savedPath) throws SVNException, IOException {
		SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(repositoryBaseUrl);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);

		Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "/" }, logEntries, revisionNum, revisionNum, true, true);

		for (SVNLogEntry logEntry : logEntries) {

			File dir1 = new File(savedPath+"/"  + revisionNum + "/new/");// 当前版本文件的存储路径
			File dir2 = new File(savedPath+"/"  + revisionNum + "/old/");// 前一个版本文件的存储路径
			File dir3 = new File(savedPath+"/"  + revisionNum + "/commitIfo");//结果文件的存储路径
			if (!dir1.exists()) {
				dir1.mkdirs();
			}
			if (!dir2.exists()) {
				dir2.mkdirs();
			}
			if(!dir3.exists()){
				dir3.mkdirs();
			}
			File logFile = new File(dir3.getPath() + "/" + logEntry.getRevision() + ".txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(logFile));

			output.write("---------------------------------------------\r\n");

			// 获取修改版本号

			output.write("修订版本号: " + logEntry.getRevision() + "\r\n");

			// 获取提交者

			output.write("提交者: " + logEntry.getAuthor() + "\r\n");

			// 获取提交时间

			output.write("日期: " + logEntry.getDate() + "\r\n");

			// 获取注释信息

			output.write("注释信息: " + logEntry.getMessage() + "\r\n");
			
			//output.write("Revision Properties: " + logEntry. + "\r\n");

			if (logEntry.getChangedPaths().size() > 0) {

				output.write("受影响的文件、目录:\r\n");

				for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {

					output.write(entry.getValue() + "\r\n");

					// 此变量用来存放要查看的文件的属性名/属性值列表。
					SVNProperties fileProperties = new SVNProperties();

					String temp = entry.getValue().toString();
					String filePath = "";// 文件的下载路径
					if (temp.indexOf("(") > 0) {
						filePath = temp.substring(temp.indexOf("/"), temp.indexOf("("));
					} else {
						filePath = temp.substring(temp.indexOf("/"));
					}

					String[] temps = filePath.split("/");
					String srcname = temps[temps.length - 1];// 获得文件名，不含路径

					// 只提取java文件
					if (srcname.endsWith(".java")) {
						if (repository.checkPath(filePath, logEntry.getRevision()) != SVNNodeKind.NONE) {
							// 此输出流用来存放要查看的文件的内容。
							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();// 当前版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision(), fileProperties, outputStream1);
							BufferedWriter output1 = new BufferedWriter(new FileWriter(dir1 + "/" + srcname));
							output1.write(outputStream1.toString());
							outputStream1.close();

							output1.close();
						}

						if (logEntry.getRevision() > 0
								&& repository.checkPath(filePath, logEntry.getRevision() - 1) != SVNNodeKind.NONE) {
							ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();// 前一个版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision() - 1, fileProperties, outputStream2);
							BufferedWriter output2 = new BufferedWriter(new FileWriter(dir2 + "/" + srcname));
							output2.write(outputStream2.toString());
							outputStream2.close();
							output2.close();

						}
					}
				}

			}
			output.close();

		}

	}

	

	public static void main(String[] args) throws SVNException {

		try {
			SVNHelper.download("http://svn.code.sf.net/p/jedit/svn/jEdit/trunk/", 24048, "D:/log");
			//SVNHelper.writeInfo("http://svn.code.sf.net/p/jhotdraw/svn/trunk", "D:/jhodraw21.txt");
			
			String url3 = "http://svn.code.sf.net/p/jxplorer/code/jxplorer-code";
			String url2 = "http://svn.code.sf.net/p/jhotdraw/svn/trunk";
			String url = "http://svn.code.sf.net/p/jtds/code/trunk";
			String url4 = "http://svn.code.sf.net/p/jfreechart/code/trunk";
			//SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url4);
			String url5 = "http://svn.code.sf.net/p/openrocket/code/trunk";
	       

	        
		
			//SVNHelper.downloadClientDiffCode2(url4, 21, url4, 30);
			//SVNHelper.downloadClientDiffCode(url4, 21, url4, 25);
	       //SVNHelper.downloadSVNDiffWindow("D:/diff/DrawingView.java");
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
