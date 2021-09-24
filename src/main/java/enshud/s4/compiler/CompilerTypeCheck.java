package enshud.s4.compiler;

import java.util.*;

/**
 * <p><b>名前</b>: <b>{@code CompilerTypeCheck} クラス</b>
 * <p><b>説明</b>: 型整合性を確認する、 {@link CompilerParseMain} クラスのヘルパクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerTypeCheck(CompilerLabelGen)}
 * </ul>
 */
final class CompilerTypeCheck {
	/*Identifier Type List Variables*/
	private HashMap<String, CompilerTypeData> globalTypeList;
	private HashMap<String, CompilerTypeData> localTypeList;
	
	/*Temporary Variables*/
	private ArrayList<String> stringTempList; //identifier list
	private String subroutineStringTemp = null; //subroutine identifier
	
	private ArrayList<Integer> argTypeTempList = null; //subroutine parameter(argument)s type list
	
	private int indexMinTemp = 0; //minimum index
	private int indexMaxTemp = 0; //maximum index
	
	private char signTemp = '\0'; //integer sign
	private int constantTemp = 0; //integer number
	
	private String lastString;
	
	/*Label Generator*/
	private CompilerLabelGen clg;
	
	/*Type Stack*/
	private Stack<Integer> typeStack;
	
	/**
	 * <p><b>名前</b>: <b>{@code CompilerTypeCheck(CompilerLabelGen)} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerTypeCheck}.<b>{@code CompilerParseMain()}</b>
	 * <p><b>説明</b>: {@link CompilerTypeCheck} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>:
	 * <ul>
	 * <li><b>{@code labelGen}</b>: ラベル生成器のインスタンス</li>
	 * </ul>
	 * <p><b>参照</b>:
	 * <ul>
	 * <li>{@link CompilerTypeData} クラス
	 * <li>{@link CompilerLabelGen} クラス
	 * </ul>
	 */
	public CompilerTypeCheck(CompilerLabelGen labelGen) {
		globalTypeList = new HashMap<String, CompilerTypeData>();
		localTypeList = new HashMap<String, CompilerTypeData>();
		stringTempList = new ArrayList<String>();
		typeStack = new Stack<Integer>();
		clg = labelGen;
	}
	
	/*Identifier*/
	public void addString(String str) {
		stringTempList.add(str);
	}
	
	public void clearStringList() {
		stringTempList.clear();
	}
	
	/*Type*/
	public boolean fromNormalTypeToArrayType() {
		if(CompilerTypeData.isNormalType(peekType())) {
			pushType(popType()+10);
			return true;
		}
		else return false;
	}
	
	public boolean fromArrayTypeToNormalType() {
		if(CompilerTypeData.isArrayType(peekType())) {
			pushType(popType()-10);
			return true;
		}
		else return false;
	}
	
	/*Subroutine Name*/
	public void setSubroutineString(String str) {
		subroutineStringTemp = str;
	}
	
	/*Global Identifiers*/
	private boolean addGlobalIdentifier(String str, int type, ArrayList<Integer> args, int indexMin, int indexMax) {
		if(globalTypeList.containsKey(str)) return false; //already declared
		else {
			globalTypeList.put(str, new CompilerTypeData(str, type, args, indexMin, indexMax));
			if(type != CompilerTypeData.TYPE_SUBROUTINE) clg.addVarLabel(str);
			return true;
		}
	}
	
	private boolean addGlobalIdentifier(String str, int type) {
		if(CompilerTypeData.isArrayType(type))
			return addGlobalIdentifier(str, type, null, indexMinTemp, indexMaxTemp);
		else
			return addGlobalIdentifier(str, type, null, 0, 0);
	}
	
	public boolean addGlobalIdentifiers() {
		int type = peekType();

		if(CompilerTypeData.isNormalType(type) || CompilerTypeData.isArrayType(type)) {
			for(int i = 0; i < stringTempList.size(); i++) {
				if(!addGlobalIdentifier(stringTempList.get(i), type)) return false;
			}
			popType();
			return true;
		}
		else return false;
	}
	
	public boolean addGlobalSubroutineIdentifier() {
		boolean ret = addGlobalIdentifier(subroutineStringTemp, CompilerTypeData.TYPE_SUBROUTINE, argTypeTempList, 0, 0);
		if(ret) clg.addSubLabel(subroutineStringTemp);
		return ret;
	}
	
	/*Local Identifiers*/
	private boolean addLocalIdentifier(boolean isParam, String str, int type, ArrayList<Integer> args, int indexMin, int indexMax) {
		if(localTypeList.containsKey(str)) return false; //already declared
		else {
			localTypeList.put(str, new CompilerTypeData(str, type, args, indexMin, indexMax));
			if(isParam)
				clg.addParamVar(str);
			else
				clg.addLocalVar(str, indexMax - indexMin + 1);
			return true;
		}
	}
	
	private boolean addLocalIdentifier(boolean isParam, String str, int type) {
		if(CompilerTypeData.isArrayType(type))
			return addLocalIdentifier(isParam, str, type, null, indexMinTemp, indexMaxTemp);
		else
			return addLocalIdentifier(isParam, str, type, null, 0, 0);
	}
	
	public boolean addLocalIdentifiers() {
		return addLocalIdentifiers(false);
	}
	
	public boolean addLocalIdentifiers(boolean isParam) {
		int type = peekType();
		
		if(CompilerTypeData.isNormalType(type) || CompilerTypeData.isArrayType(type)) {
			for(int i = 0; i < stringTempList.size(); i++) {
				if(!addLocalIdentifier(isParam, stringTempList.get(i), type)) return false;
			}
			popType();
			return true;
		}
		else return false;
	}
	
	public void clearLocalIdentifierList() {
		localTypeList.clear();
		clg.clearLocalVarList();
		clg.clearParamVarList();
	}
	
	/*Stack push Type*/
	public void pushType(int type) {
		typeStack.push(new Integer(type));
	}
	
	public boolean pushVariableType() {
		if(stringTempList.size() > 0) {
			String str = stringTempList.get(stringTempList.size()-1);
			
			if(localTypeList.containsKey(str)) {
				CompilerTypeData data = localTypeList.get(str);
				int type = data.getType();
				if(!(CompilerTypeData.isNormalType(type) || CompilerTypeData.isArrayType(type))) return false; //not variable
				else {
					this.pushType(type);
					stringTempList.remove(stringTempList.size()-1);
					lastString = str;
					return true;
				}
			}
			else if(globalTypeList.containsKey(str)) {
				CompilerTypeData data = globalTypeList.get(str);
				int type = data.getType();
				if(!(CompilerTypeData.isNormalType(type) || CompilerTypeData.isArrayType(type))) return false; //not variable
				else {
					this.pushType(type);
					stringTempList.remove(stringTempList.size()-1);
					lastString = str;
					return true;
				}
			}
			else return false; //not declared
		}
		else {
			return false;
		}
	}
	
	public boolean pushSubroutineArgumentsType() {
		if(subroutineStringTemp != null) {
			if(globalTypeList.containsKey(subroutineStringTemp)) {
				CompilerTypeData data = globalTypeList.get(subroutineStringTemp);
				if(data.getType() != CompilerTypeData.TYPE_SUBROUTINE) return false; //not subroutine
				else {
					this.pushType(CompilerTypeData.TYPE_SUBROUTINE);
					data.stackArgumentsType(typeStack);
					return true;
				}
			}
			else return false; //not declared
		}
		else {
			return false;
		}
	}
	
	/*Stack pop Type*/
	public int popType() {
		if(typeStack.size() > 0)
			return typeStack.pop().intValue();
		else
			return CompilerTypeData.TYPE_NONE;
	}
	
	/*Stack peek Type*/
	public int peekType() {
		if(typeStack.size() > 0)
			return typeStack.peek().intValue();
		else
			return CompilerTypeData.TYPE_NONE;
	}
	
	/*Stack check Type*/
	public boolean checkType(boolean noLeft) {
		if(typeStack.size() >= 2) {
			int type1 = popType();
			int type2 = peekType();
			if(type1 == type2) {
				if(noLeft) popType();
				return true;
			}
			else {
				pushType(type1);
				return false;
			}
		}
		else return false;
	}
	
	/*Argument Type*/
	public void addArgumentTypes() {
		int type = peekType();
		
		if(argTypeTempList == null) {
			argTypeTempList = new ArrayList<Integer>();
		}
		
		for(int i = stringTempList.size(); i > 0; i--) {
			argTypeTempList.add(type);
		}
	}
	
	public void clearArgumentTypeList() {
		argTypeTempList = null;
	}
	
	/*Constant*/
	public boolean setNumberConstant(String str) {
		try {
			int temp = Integer.parseUnsignedInt(str);
			if(temp >= 0 && temp < 65536) {
				this.constantTemp = temp;
				return true;
			}
			else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public void setSign(char sign) {
		this.signTemp = sign;
	}
	
	public char getSign() {
		return signTemp;
	}
	
	/*Index*/
	private boolean setIndex(boolean isMin) {
		if(signTemp == '-' && constantTemp <= 32768) {
			if(isMin)
				indexMinTemp = -constantTemp;
			else
				indexMaxTemp = -constantTemp;
			return true;
		}
		else if((signTemp == '+' || signTemp == '\0') && constantTemp <= 32767) {
			if(isMin)
				indexMinTemp = constantTemp;
			else
				indexMaxTemp = constantTemp;
			return true;
		}
		else return false;
	}
	
	public boolean setIndexMin() {
		return setIndex(true);
	}
	
	public boolean setIndexMax() {
		return setIndex(false);
	}
	
	public boolean checkIndex() {
		return (indexMinTemp <= indexMaxTemp);
	}
	
	public int getLastIndexMin() {
		if(localTypeList.containsKey(lastString)) {
			CompilerTypeData data = localTypeList.get(lastString);
			return data.getIndexMin();
		}
		else if(globalTypeList.containsKey(lastString)) {
			CompilerTypeData data = globalTypeList.get(lastString);
			return data.getIndexMin();
		}
		else return 0;
	}
	
	public int getLastArraySize() {
		if(localTypeList.containsKey(lastString)) {
			CompilerTypeData data = localTypeList.get(lastString);
			return data.getIndexMax() - data.getIndexMin() + 1;
		}
		else if(globalTypeList.containsKey(lastString)) {
			CompilerTypeData data = globalTypeList.get(lastString);
			return data.getIndexMax() - data.getIndexMin() + 1;
		}
		else return 0;
	}
	
	public int getVarSize(String str) {
		if(globalTypeList.containsKey(str)) {
			CompilerTypeData data = globalTypeList.get(str);
			if(CompilerTypeData.isArrayType(data.getType()))
				return data.getIndexMax() - data.getIndexMin() + 1;
			else
				return 1;
		}
		else return 0;
	}
	
	/*Label*/
	public int getLastGlobalLabel() {
		return clg.getVarLabel(lastString);
	}
	
	public int getLastLocalLabel() {
		return clg.getLocalVar(lastString);
	}
	
	public int getLastParamLabel() {
		return clg.getParamVar(lastString);
	}
	
	public String getLastSubLabel() {
		return clg.getSubLabel(subroutineStringTemp);
	}
	
	/*Parameter Size*/
	public int getLastSubParameterSize() {
		if(globalTypeList.containsKey(subroutineStringTemp)) {
			CompilerTypeData data = globalTypeList.get(subroutineStringTemp);
			if(data.getType() == CompilerTypeData.TYPE_SUBROUTINE) {
				return data.getParameterSize();
			}
		}
		return 0;
	}
}
