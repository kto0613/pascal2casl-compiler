package enshud.s4.compiler;

import java.util.*;

/**
 * <p><b>名前</b>: <b>{@code CompilerTypeData} クラス</b>
 * <p><b>説明</b>: タイプデータを保存する、 {@link CompilerTypeCheck} クラスのヘルパクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerTypeData(String, int, ArrayList, int, int)}
 * </ul>
 */
final class CompilerTypeData {
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
	
	/**
	 * <p><b>名前</b>: <b>{@code CompilerTypeData(String, int, ArrayList, int, int)} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerTypeData}.<b>{@code CompilerTypeData(String, int, ArrayList, int, int)}</b>
	 * <p><b>説明</b>: {@link CompilerTypeData} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>:
	 * <ul>
	 * <li><b>{@code identifier}</b>: 識別子の名前</li>
	 * <li><b>{@code type}</b>: 識別子の型</li>
	 * <li><b>{@code arguments}</b>: サブルーチンの引数目録</li>
	 * <li><b>{@code indexMin}</b>: 配列の添字の最小値</li>
	 * <li><b>{@code indexMax}</b>: 配列の添字の最大値</li>
	 * </ul>
	 */
	public CompilerTypeData(String identifier, int type, ArrayList<Integer> arguments, int indexMin, int indexMax) {
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
	
	public int getParameterSize() {
		if(arguments != null) {
			return arguments.size();
		}
		else return 0;
	}
	
	public static boolean isNormalType(int type) {
		if(type >= 11 && type <= 13) return true;
		else return false;
	}
	
	public static boolean isArrayType(int type) {
		if(type >= 21 && type <= 23) return true;
		else return false;
	}
}
