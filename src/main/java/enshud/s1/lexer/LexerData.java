package enshud.s1.lexer;

/**
 * {@link LexerHelper LexerHelper} クラスの
 * 静的データおよびデータ・レコード用のクラスです。
 */
final class LexerData {
	/**
	 * トークン・シンボルの静的文字列データです。
	 */
	public static final String SymbolData[] = {
			"and", "array", "begin", "boolean", "char",
			"div", "do", "else", "end", "false",
			"if", "integer", "mod", "not", "of",
			"or", "procedure", "program", "readln", "then",
			"true", "var", "while", "writeln", "=",
			"<>", "<", "<=", ">=", ">",
			"+", "-", "*", "(", ")",
			"[", "]", ";", ":", "..",
			":=", ",", "."
	};
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
	
	private final String string;
	private final int tokenID;
	private final int lineNum;
	
	/**
	 * データ・レコーディングのための
	 * LexerData クラスのコンストラクタです。
	 * @param string
	 * レコードに登録する文字列
	 * @param tokenID
	 * トークンID
	 * @param lineNum
	 * 行番号
	 */
	LexerData(String string, int tokenID, int lineNum){
		this.string = string;
		this.tokenID = tokenID;
		this.lineNum = lineNum;
	}
	
	/**
	 * 文字列からトークンIDを取得します。
	 * @param str
	 * トークンIDを取得する文字列
	 * @return
	 * トークンIDをリターンします。
	 * トークンIDが見つからなかった場合は -1 をリターンします。
	 */
	public static int getTokenIDfromString(String str) {
		for(int i = 0; i < SymbolData.length; i++) {
			if(str.equals(SymbolData[i])) return i;
		}
		if(str.equals("/"))
			return 5;
		else
			return -1;
	}
	
	/**
	 * トークンIDからトークン名を取得します。
	 * @param id
	 * トークンID
	 * @return
	 * トークン名をリターンします。
	 * トークンIDに対応するトークン名がない場合は {@code "!UNDEFINED"} をリターンします。
	 */
	public static String getTokenNamefromTokenID(int id) {
		if(id >= 0 && id < TokenNameData.length)
			return TokenNameData[id];
		else
			return "!UNDEFINED";
	}
	
	/**
	 * データ・レコードから ts 書式の文字列を作成します。
	 * @return
	 * 作成した ts 書式の文字列です。
	 */
	public String getTSData() {
		return (string + "\t" + getTokenNamefromTokenID(tokenID) + "\t" + tokenID + "\t" + lineNum);
	}
}
