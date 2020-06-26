package cn.edu.sysu.SVNDigger;

import java.io.OutputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

public class SVNEditor implements ISVNEditor{

	@Override
	public void applyTextDelta(String arg0, String arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputStream textDeltaChunk(String arg0, SVNDiffWindow arg1) throws SVNException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void textDeltaEnd(String arg0) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abortEdit() throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void absentDir(String arg0) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void absentFile(String arg0) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDir(String arg0, String arg1, long arg2) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFile(String arg0, String arg1, long arg2) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeDirProperty(String arg0, SVNPropertyValue arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeFileProperty(String arg0, String arg1, SVNPropertyValue arg2) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeDir() throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SVNCommitInfo closeEdit() throws SVNException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeFile(String arg0, String arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(String arg0, long arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openDir(String arg0, long arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openFile(String arg0, long arg1) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openRoot(long arg0) throws SVNException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void targetRevision(long arg0) throws SVNException {
		// TODO Auto-generated method stub
		
	}

}
