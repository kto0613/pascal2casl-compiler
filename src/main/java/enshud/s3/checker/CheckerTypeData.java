package enshud.s3.checker;

import java.util.*;

final class CheckerTypeData {
	public static final int
			TYPE_NONE = 0,
			TYPE_SUBROUTINE = 1,
			TYPE_INTEGER = 11,
			TYPE_CHAR = 12,
			TYPE_BOOLEAN = 13,
			TYPE_ARRAY_INTEGER = 21,
			TYPE_ARRAY_CHAR = 22,
			TYPE_ARRAY_BOOLEAN = 23;
	
	private final String identifier;
	private final int type;
	private final ArrayList<Integer> arguments;
	private final int indexMin;
	private final int indexMax;
	
	CheckerTypeData(String identifier, int type, ArrayList<Integer> arguments, int indexMin, int indexMax) {
		this.identifier = identifier;
		this.type = type;
		this.arguments = arguments;
		this.indexMin = indexMin;
		this.indexMax = indexMax;
	}
	
	public String getIdentifier() { return identifier; }
	public int getType() { return type; }
	
	public void stackArgumentsType(Stack<Integer> stack) {
		if(arguments != null) {
			for(int i = arguments.size() - 1; i >= 0; i--) {
				stack.push(arguments.get(i));
			}
		}
	}
	
	public int getIndexMin() { return indexMin; }
	public int getIndexMax() { return indexMax; }
	
	public static boolean isNormalType(int type) {
		if(type >= 11 && type <= 13) return true;
		else return false;
	}
	
	public static boolean isArrayType(int type) {
		if(type >= 21 && type <= 23) return true;
		else return false;
	}
}
