package enshud.s4.compiler;

import java.util.*;

/**
 * <p><b>名前</b>: <b>{@code CompilerCodeGen} クラス</b>
 * <p><b>説明</b>: CASL IIアセンブリコードを生成する、 {@link CompilerParseMain} クラスのヘルパクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerCodeGen()}
 * </ul>
 */
final class CompilerCodeGen {

	private ArrayList<String> mainCodeList;
	private ArrayList<String> subCodeList;
	private ArrayList<String> tempCodeList;
	
	private boolean isMain;
	
	/**
	 * <p><b>名前</b>: <b>{@code CompilerCodeGen()} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerCodeGen}.<b>{@code CompilerCodeGen()}</b>
	 * <p><b>説明</b>: {@link CompilerCodeGen} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>: 無し
	 */
	public CompilerCodeGen() {
		mainCodeList = new ArrayList<String>();
		subCodeList = new ArrayList<String>();
		tempCodeList = new ArrayList<String>();
		isMain = false;
	}
	
	public void add(String code, String label) {
		tempCodeList.add(label + "\t" + code);
	}
	
	public void add(String code) {
		tempCodeList.add("\t" + code);
	}
	
	public void setFlush(boolean isMain) {
		this.isMain = isMain;
	}
	
	public void flush() {
		flush(isMain);
	}
	
	public void flush(boolean isMain) {
		if(isMain)
			mainCodeList.addAll(tempCodeList);
		else
			subCodeList.addAll(tempCodeList);
		tempCodeList.clear();
	}
	
	public void appendSub() {
		mainCodeList.addAll(subCodeList);
	}
	
	public void clear() {
		tempCodeList.clear();
	}
	
	public ArrayList<String> getCodeList() {
		return mainCodeList;
	}
	
	//TODO: error-check
	public void addComment(String comment) {
		tempCodeList.add(";" + comment);
	}

}
