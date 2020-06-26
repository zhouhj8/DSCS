package cn.edu.sysu.syntaxsimilar;

import java.util.ArrayList;

public class CodeFragment {
	private int startLine;
    private int endLine;
	private int statementsNum;
	private ArrayList<Statement> statements = new ArrayList<Statement>();
		
    public CodeFragment(int startLine, int endLine, int statementsNum, ArrayList<Statement> statements) {
		super();
		this.startLine = startLine;
		this.endLine = endLine;
		this.statementsNum = statementsNum;
		this.statements = statements;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public int getStatementsNum() {
		return statementsNum;
	}
	public void setStatementsNum(int statementsNum) {
		this.statementsNum = statementsNum;
	}
	public ArrayList<Statement> getStatements() {
		return statements;
	}
	public void setStatements(ArrayList<Statement> statements) {
		this.statements = statements;
	}
		

}
