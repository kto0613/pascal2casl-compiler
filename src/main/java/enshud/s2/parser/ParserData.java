package enshud.s2.parser;

/**
 * ParserData クラスです。
 */
final class ParserData {
	/**トークンID*/
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
	/**
	 * トークン名の静的文字列データです。
	 */
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
	
	private final int tokenID;
	private final int lineNum;
	
	/**
	 * ParserData クラスのコンストラクタです。
	 * @param tokenID
	 * トークンID
	 * @param lineNum
	 * 行番号
	 */
	ParserData(int tokenID, int lineNum) {
		this.tokenID = tokenID;
		this.lineNum = lineNum;
	}
	
	/**
	 * トークンIDに対応するトークン名をリターンします。
	 * @param tokenID
	 * トークンID
	 * @return
	 * トークンIDに対応するトークン名です。
	 */
	public static String getTokenNameof(int tokenID) {
		if(tokenID >= 0 && tokenID < TokenNameData.length)
			return TokenNameData[tokenID];
		else
			return null;
	}
	
	/**
	 * データレコードのトークンIDをリターンします。
	 * @return
	 * トークンIDです。
	 */
	public int getTokenID() { return tokenID; }
	/**
	 * データレコードの行番号をリターンします。
	 * @return
	 * 行番号です。
	 */
	public int getLineNum() { return lineNum; }
}
