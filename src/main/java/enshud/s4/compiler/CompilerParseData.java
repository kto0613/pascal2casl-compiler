package enshud.s4.compiler;

/**
 * <p><b>名前</b>: <b>{@code CompilerParseData} クラス</b>
 * <p><b>説明</b>: 構文解析データを保存する、 {@link CompilerParseMain} クラスのヘルパクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerParseData(String, int, int)}
 * </ul>
 */
final class CompilerParseData {
	/*Token ID*/
	public static final int
			SAND = 0,
			SARRAY = 1,
			SBEGIN = 2,
			SBOOLEAN = 3,
			SCHAR = 4,
			SDIVD = 5,
			SDO = 6,
			SELSE = 7,
			SEND = 8,
			SFALSE = 9,
			
			SIF = 10,
			SINTEGER = 11,
			SMOD = 12,
			SNOT = 13,
			SOF = 14,
			SOR = 15,
			SPROCEDURE = 16,
			SPROGRAM = 17,
			SREADLN = 18,
			STHEN = 19,
			
			STRUE = 20,
			SVAR = 21,
			SWHILE = 22,
			SWRITELN = 23,
			SEQUAL = 24,
			SNOTEQUAL = 25,
			SLESS = 26,
			SLESSEQUAL = 27,
			SGREATEQUAL = 28,
			SGREAT = 29,
			
			SPLUS = 30,
			SMINUS = 31,
			SSTAR = 32,
			SLPAREN = 33,
			SRPAREN = 34,
			SLBRACKET = 35,
			SRBRACKET = 36,
			SSEMICOLON = 37,
			SCOLON = 38,
			SRANGE = 39,
			
			SASSIGN = 40,
			SCOMMA = 41,
			SDOT = 42,
			SIDENTIFIER = 43,
			SCONSTANT = 44,
			SSTRING = 45;
	
	/*Static data of Token Name*/
	public static final String TokenNameData[] = {
			"SAND", "SARRAY", "SBEGIN", "SBOOLEAN", "SCHAR",
			"SDIVD", "SDO", "SELSE", "SEND", "SFALSE",
			"SIF", "SINTEGER", "SMOD", "SNOT", "SOF",
			"SOR", "SPROCEDURE", "SPROGRAM", "SREADLN", "STHEN",
			"STRUE", "SVAR", "SWHILE", "SWRITELN", "SEQUAL",
			"SNOTEQUAL", "SLESS", "SLESSEQUAL", "SGREATEQUAL", "SGREAT",
			"SPLUS", "SMINUS", "SSTAR", "SLPAREN", "SRPAREN",
			"SLBRACKET", "SRBRACKET", "SSEMICOLON", "SCOLON", "SRANGE",
			"SASSIGN", "SCOMMA", "SDOT", "SIDENTIFIER", "SCONSTANT",
			"SSTRING"
	};
	
	/*Variables*/
	private final String string;
	private final int tokenID;
	private final int lineNum;
	
	/**
	 * <p><b>名前</b>: <b>{@code CompilerParseData(String, int, int)} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerParseData}.<b>{@code CompilerParseData(String, int, int)}</b>
	 * <p><b>説明</b>: {@link CompilerParseData} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>:
	 * <ul>
	 * <li><b>{@code string}</b>: 識別記号(トークン)</li>
	 * <li><b>{@code tokenID}</b>: 識別記号のID</li>
	 * <li><b>{@code lineNum}</b>: 元PASファイルでの行番号</li>
	 * </ul>
	 */
	CompilerParseData(String string, int tokenID, int lineNum) {
		this.string = string;
		this.tokenID = tokenID;
		this.lineNum = lineNum;
	}
	
	/*Static methods*/
	public static String getTokenNameof(int tokenID) {
		if(tokenID >= 0 && tokenID < TokenNameData.length)
			return TokenNameData[tokenID];
		else
			return null;
	}
	
	/*Getters*/
	public String getString() { return string; }
	public int getTokenID() { return tokenID; }
	public int getLineNum() { return lineNum; }
}
