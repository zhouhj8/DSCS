package cn.edu.sysu.SVNDigger;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class CommitDiff {
	
	public static void downloadJavaFile(String url, int revisionNum, String savedPath) throws SVNException, IOException {
		SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(repositoryBaseUrl);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);

		Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "/" }, logEntries, revisionNum, revisionNum, true, true);

		for (SVNLogEntry logEntry : logEntries) {

			File dir1 = new File(savedPath+"/"  + revisionNum + "/new/");// ��ǰ�汾�ļ��Ĵ洢·��
			File dir2 = new File(savedPath+"/"  + revisionNum + "/old/");// ǰһ���汾�ļ��Ĵ洢·��
			File dir3 = new File(savedPath+"/"  + revisionNum + "/commitIfo");//����ļ��Ĵ洢·��
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

			// ��ȡ�޸İ汾��

			output.write("�޶��汾��: " + logEntry.getRevision() + "\r\n");

			// ��ȡ�ύ��

			output.write("�ύ��: " + logEntry.getAuthor() + "\r\n");

			// ��ȡ�ύʱ��

			output.write("����: " + logEntry.getDate() + "\r\n");

			// ��ȡע����Ϣ

			output.write("ע����Ϣ: " + logEntry.getMessage() + "\r\n");
			
			//output.write("Revision Properties: " + logEntry. + "\r\n");

			if (logEntry.getChangedPaths().size() > 0) {

				output.write("��Ӱ����ļ���Ŀ¼:\r\n");

				for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {

					output.write(entry.getValue() + "\r\n");

					// �˱����������Ҫ�鿴���ļ���������/����ֵ�б�
					SVNProperties fileProperties = new SVNProperties();

					String temp = entry.getValue().toString();
					String filePath = "";// �ļ�������·��
					if (temp.indexOf("(") > 0) {
						filePath = temp.substring(temp.indexOf("/"), temp.indexOf("("));
					} else {
						filePath = temp.substring(temp.indexOf("/"));
					}

					String[] temps = filePath.split("/");
					String srcname = temps[temps.length - 1];// ����ļ���������·��

					// ֻ��ȡjava�ļ�
					if (srcname.endsWith(".java")) {
						if (repository.checkPath(filePath, logEntry.getRevision()) != SVNNodeKind.NONE) {
							// ��������������Ҫ�鿴���ļ������ݡ�
							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();// ��ǰ�汾���ļ������

							repository.getFile(filePath, logEntry.getRevision(), fileProperties, outputStream1);
							BufferedWriter output1 = new BufferedWriter(new FileWriter(dir1 + "/" + srcname));
							output1.write(outputStream1.toString());
							outputStream1.close();

							output1.close();
						}

						if (logEntry.getRevision() > 0
								&& repository.checkPath(filePath, logEntry.getRevision() - 1) != SVNNodeKind.NONE) {
							ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();// ǰһ���汾���ļ������

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
			String url3 = "http://svn.code.sf.net/p/jxplorer/code/jxplorer-code";
			String url2 = "http://svn.code.sf.net/p/jhotdraw/svn/trunk";
			String url1 = "http://svn.code.sf.net/p/jtds/code/trunk";
			String url4 = "http://svn.code.sf.net/p/jfreechart/code/trunk";
			String url5 = "http://svn.code.sf.net/p/openrocket/code/trunk";
			
			for(int i=535; i<25000; i++){
				CommitDiff.downloadJavaFile("http://svn.code.sf.net/p/jedit/svn/jEdit/trunk/", i, "D:/log1");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
