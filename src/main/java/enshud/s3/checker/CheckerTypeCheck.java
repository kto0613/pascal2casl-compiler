package enshud.s3.checker;

import java.util.*;

final class CheckerTypeCheck {
	/*Identifier Type List Variables*/
	private HashMap<String, CheckerTypeData> globalTypeList;
	private HashMap<String, CheckerTypeData> localTypeList;
	
	/*Temporary Variables*/
	private ArrayList<String> stringTempList; //identifier list
	private String subroutineStringTemp = null; //subroutine identifier
	
	private ArrayList<Integer> argTypeTempList = null; //subroutine parameter(argument)s type list
	
	private int indexMinTemp = 0; //minimal index
	private int indexMaxTemp = 0; //maximum index
	
	private char signTemp = '\0'; //integer sign
	private int constantTemp = 0; //integer number
	
	/*Type Stack*/
	private Stack<Integer> typeStack;
	
	/**Constructor of CheckerTypeCheck*/
	CheckerTypeCheck() {
		globalTypeList = new HashMap<String, CheckerTypeData>();
		localTypeList = new HashMap<String, CheckerTypeData>();
		stringTempList = new ArrayList<String>();
		typeStack = new Stack<Integer>();
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
		if(CheckerTypeData.isNormalType(peekType())) {
			pushType(popType()+10);
			return true;
		}
		else return false;
	}
	
	public boolean fromArrayTypeToNormalType() {
		if(CheckerTypeData.isArrayType(peekType())) {
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
			globalTypeList.put(str, new CheckerTypeData(str, type, args, indexMin, indexMax));
			return true;
		}
	}
	
	private boolean addGlobalIdentifier(String str, int type) {
		if(CheckerTypeData.isArrayType(type))
			return addGlobalIdentifier(str, type, null, indexMinTemp, indexMaxTemp);
		else
			return addGlobalIdentifier(str, type, null, 0, 0);
	}
	
	public boolean addGlobalIdentifiers() {
		int type = peekType();

		if(CheckerTypeData.isNormalType(type) || CheckerTypeData.isArrayType(type)) {
			for(int i = stringTempList.size() - 1; i >= 0; i--) {
				if(!addGlobalIdentifier(stringTempList.get(i), type)) return false;
			}
			popType();
			return true;
		}
		else return false;
	}
	
	public boolean addGlobalSubroutineIdentifier() {
		return addGlobalIdentifier(subroutineStringTemp, CheckerTypeData.TYPE_SUBROUTINE, argTypeTempList, 0, 0);
	}
	
	/*Local Identifiers*/
	private boolean addLocalIdentifier(String str, int type, ArrayList<Integer> args, int indexMin, int indexMax) {
		if(localTypeList.containsKey(str)) return false; //already declared
		else {
			localTypeList.put(str, new CheckerTypeData(str, type, args, indexMin, indexMax));
			return true;
		}
	}
	
	private boolean addLocalIdentifier(String str, int type) {
		if(CheckerTypeData.isArrayType(type))
			return addLocalIdentifier(str, type, null, indexMinTemp, indexMaxTemp);
		else
			return addLocalIdentifier(str, type, null, 0, 0);
	}
	
	public boolean addLocalIdentifiers() {
		int type = peekType();
		
		if(CheckerTypeData.isNormalType(type) || CheckerTypeData.isArrayType(type)) {
			for(int i = stringTempList.size() - 1; i >= 0; i--) {
				if(!addLocalIdentifier(stringTempList.get(i), type)) return false;
			}
			popType();
			return true;
		}
		else return false;
	}
	
	public void clearLocalIdentifierList() {
		localTypeList.clear();
	}
	
	/*Stack push Type*/
	public void pushType(int type) {
		typeStack.push(new Integer(type));
	}
	
	public boolean pushVariableType() {
		if(stringTempList.size() > 0) {
			String str = stringTempList.get(stringTempList.size()-1);
			
			if(localTypeList.containsKey(str)) {
				CheckerTypeData data = localTypeList.get(str);
				int type = data.getType();
				if(!(CheckerTypeData.isNormalType(type) || CheckerTypeData.isArrayType(type))) return false; //not variable
				else {
					this.pushType(type);
					stringTempList.remove(stringTempList.size()-1);
					return true;
				}
			}
			else if(globalTypeList.containsKey(str)) {
				CheckerTypeData data = globalTypeList.get(str);
				int type = data.getType();
				if(!(CheckerTypeData.isNormalType(type) || CheckerTypeData.isArrayType(type))) return false; //not variable
				else {
					this.pushType(type);
					stringTempList.remove(stringTempList.size()-1);
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
				CheckerTypeData data = globalTypeList.get(subroutineStringTemp);
				if(data.getType() != CheckerTypeData.TYPE_SUBROUTINE) return false; //not subroutine
				else {
					this.pushType(CheckerTypeData.TYPE_SUBROUTINE);
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
			return CheckerTypeData.TYPE_NONE;
	}
	
	/*Stack peek Type*/
	public int peekType() {
		if(typeStack.size() > 0)
			return typeStack.peek().intValue();
		else
			return CheckerTypeData.TYPE_NONE;
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
	
	public boolean hasSign() {
		if(signTemp == '+' || signTemp == '-') return true;
		else return false;
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
}
