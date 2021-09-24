package enshud.s4.compiler;

import java.util.*;

/**
 * <p><b>名前</b>: <b>{@code CompilerLabelGen} クラス</b>
 * <p><b>説明</b>: ラベルを生成する、 {@link CompilerParseMain} クラスのヘルパクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerLabelGen()}
 * </ul>
 */
final class CompilerLabelGen {
	
	private HashMap<String, String> subLabelList;	//Label for subroutines
	private ArrayList<String> varLabelList;	//Label for global variables
	private ArrayList<String> strDataLabelList;	//Label for const string data
	private ArrayList<String> localVarList;	//Label for local variables (on stack)
	private ArrayList<String> paramVarList;
	
	private int nNormalLabel;	//Label for if..then..else, while statements
	private int nSubLabel;

	/**
	 * <p><b>名前</b>: <b>{@code CompilerLabelGen()} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerLabelGen}.<b>{@code CompilerLabelGen()}</b>
	 * <p><b>説明</b>: {@link CompilerLabelGen} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>: 無し
	 */
	public CompilerLabelGen() {
		subLabelList = new HashMap<String, String>();
		varLabelList = new ArrayList<String>();
		strDataLabelList = new ArrayList<String>();
		localVarList = new ArrayList<String>();
		paramVarList = new ArrayList<String>();
		nNormalLabel = nSubLabel = 0;
	}
	
	/*Normal Label*/
	public int genNormalLabel() {
		return ++nNormalLabel;
	}
	
	/*Sub Label List*/
	public void addSubLabel(String subroutine) {
		subLabelList.put(subroutine, "SUB" + ++nSubLabel);
	}
	
	public String getSubLabel(String subroutine) {
		return subLabelList.get(subroutine);
	}
	
	/*Variable Label List*/
	public void addVarLabel(String variable) {
		varLabelList.add(variable);
	}
	
	public int getVarLabel(String variable) {
		int i = varLabelList.indexOf(variable);
		if(i < 0)
			return -1;
		else
			return (i + 1);
	}
	
	/*String Data Label List*/
	public int getStrDataLabel(String stringData) {
		if(!strDataLabelList.contains(stringData)) {
			strDataLabelList.add(stringData);
		}
		return (strDataLabelList.indexOf(stringData) + 1);
	}
	
	/*Local Variable List*/
	public void addLocalVar(String variable, int length) {
		localVarList.add(variable);
		for(int i = 1; i < length; i++) {
			localVarList.add(null);
		}
	}
	
	public int getLocalVar(String variable) {
		return localVarList.indexOf(variable); //LAD r, addr, GR5
	}
	
	public int getLocalVarSize() {
		return localVarList.size();
	}
	
	public void clearLocalVarList() {
		localVarList.clear();
	}
	
	/*Parameter(Argument) Variable List*/
	public void addParamVar(String variable) {
		paramVarList.add(variable);
	}
	
	public int getParamVar(String variable) {
		int i = paramVarList.indexOf(variable);
		if(i < 0)
			return -1;
		else
			return (paramVarList.size() - i - 1); //LAD r, addr, GR4
	}
	
	public int getParamVarSize() {
		return paramVarList.size();
	}
	
	public void clearParamVarList() {
		paramVarList.clear();
	}
	
	/*Getters*/
	public ArrayList<String> getVarLabelList(){
		return varLabelList;
	}
	
	public ArrayList<String> getStrDataLabelList(){
		return strDataLabelList;
	}

}
