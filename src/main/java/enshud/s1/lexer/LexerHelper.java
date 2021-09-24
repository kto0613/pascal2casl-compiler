package enshud.s1.lexer;

import java.io.*;
import java.util.*;

/**
 * {@link enshud.s1.lexer.Lexer Lexer} クラスのヘルパクラスです。
 */
final class LexerHelper {
	private final String inputFileName;
	private final String outputFileName;
	
	private BufferedReader inputFile = null;
	private ArrayList<LexerData> list = null;
	
	private int c;
	private String string;
	private int lineNum;
	
	/**
	 * LexerHelper クラスのコンストラクタです。
	 * @param inputFileName
	 * 入力ファイル名 (*.pas)
	 * @param outputFileName
	 * 出力ファイル名 (*.ts)
	 */
	LexerHelper(final String inputFileName, final String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
	}
	
	/**
	 * pas 字句解析を開始します。既に字句解析を開始している場合は何もせずにリターンします。
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void Start() throws FileNotFoundException, IOException {
		if(inputFile == null) {
			inputFile = new BufferedReader(new FileReader(inputFileName));
			list = new ArrayList<LexerData>();
			c = inputFile.read();
			string = "";
			lineNum = 1;
		}
	}
	
	/**
	 * 入力ファイル (*.pas) から読み込んだ文字をリターンします。
	 * このメソッドを呼び出す前に必ず {@linkplain #Start() Start} メソッドで
	 * pas 字句解析を開始してください。
	 * @return
	 * 現在の文字をリターンします。
	 */
	public int C() {
		if(inputFile != null)
			return c;
		else
			return -1;
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) をトークン文字列の最後に併合します
	 * (pas 字句解析の開始直後にはトークン文字列は空の状態です)。
	 * また、入力 pas ファイルから次の文字を読み込みます。
	 * このメソッドを呼び出す前に必ず {@linkplain #Start() Start} メソッドで
	 * pas 字句解析を開始してください。
	 * @throws IOException
	 */
	public void Append() throws IOException {
		if(inputFile != null) {
			if(c == '\n') lineNum++;
			string += (char)c; c = inputFile.read();
		}
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) を読み飛ばし、
	 * 入力 pas ファイルから次の文字を読み込みます。
	 * このメソッドを呼び出す前に必ず {@linkplain #Start() Start} メソッドで
	 * pas 字句解析を開始してください。
	 * @throws IOException
	 */
	public void Skip() throws IOException {
		if(inputFile != null) {
			if(c == '\n') lineNum++;
			c = inputFile.read();
		}
	}
	
	/**
	 * トークン文字列からトークンIDを調べます。
	 * このメソッドを呼び出す前に必ず {@linkplain #Start() Start} メソッドで
	 * pas 字句解析を開始してください。
	 * @return
	 * トークンIDをリターンします。
	 * トークン文字列に対応するトークンIDが見つからない場合や
	 * pas 字句解析をまだ開始していない場合は -1 をリターンします。
	 */
	public int getTokenID() {
		if(inputFile != null)
			return LexerData.getTokenIDfromString(string);
		else
			return -1;
	}
	
	/**
	 * 現在のトークン文字列をデータ・レコードに登録し、トークン文字列を空にします。
	 * このメソッドを呼び出す前に必ず {@linkplain #Start() Start} メソッドで
	 * pas 字句解析を開始してください。
	 * @param tokenID
	 * 登録するトークン文字列のトークンID
	 */
	public void Register(int tokenID) {
		if(inputFile != null) {
			list.add(new LexerData(string, tokenID, lineNum));
			string = "";
		}
	}
	
	/**
	 * pas 字句解析を終了します。
	 */
	public void End() {
		if(inputFile != null) {
			try {
				inputFile.close();
			} catch (IOException e) {
				//Close silently...
			}
			inputFile = null;
		}
	}
	
	/**
	 * pas 字句解析の結果を出力ファイル (*.ts) に書き込みます。
	 * このメソッドが正しく動作するためには、
	 * {@link #Start() Start} メソッドから始まり、
	 * {@link #End() End} メソッドで終わる
	 * pas 字句解析ルーチンを一回以上実行する必要があります。
	 * @throws IOException
	 */
	public void writeTSFile() throws IOException {
		if (inputFile == null && list != null) {
			BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName));
			for (int i = 0; i < list.size(); i++) {
				LexerData data = list.get(i);
				outputFile.write(data.getTSData());
				outputFile.newLine();
			}
			outputFile.close();
			list = null;
		}
	}
	
	protected void finalize() {
			this.End();
	}
	
	public static boolean isAlpha(int c) {
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		else
			return false;
	}
	
	public static boolean isNum(int c) {
		if(c >= '0' && c <= '9')
			return true;
		else
			return false;
	}
	
	public static boolean isAlnum(int c) {
		return (isAlpha(c) || isNum(c));
	}
	
	public static boolean isSeparator(int c) {
		if(c == ' ' || c == '\t' || c == '\n' || c == '\r')
			return true;
		else
			return false;
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) がアルファベット(英字)なのか調べます。
	 * @return
	 * 現在の文字がアルファベットであれば {@code true}、
	 * そうでなければ {@code false} をリターンします。
	 */
	public boolean CisAlpha() {
		if(inputFile != null)
			return isAlpha(c);
		else
			return false;
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) が数字なのか調べます。
	 * @return
	 * 現在の文字が数字であれば {@code true}、
	 * そうでなければ {@code false} をリターンします。
	 */
	public boolean CisNum() {
		if(inputFile != null)
			return isNum(c);
		else
			return false;
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) がアルファベット(英字)、
	 * もしくは数字なのか調べます。
	 * @return
	 * 現在の文字がアルファベットか数字であれば {@code true}、
	 * そうでなければ {@code false} をリターンします。
	 */
	public boolean CisAlnum() {
		if(inputFile != null)
			return isAlnum(c);
		else
			return false;
	}
	
	/**
	 * 現在の文字 ({@link #C() C} メソッドのリターン値) が記号分離子なのか調べます。
	 * @return
	 * 現在の文字が記号分離子であれば {@code true}、
	 * そうでなければ {@code false} をリターンします。
	 */
	public boolean CisSeparator() {
		if(inputFile != null)
			return isSeparator(c);
		else
			return false;
	}
	
	/**
	 * エラーの種類を表す列挙型です。
	 */
	public enum Error {
		/** 不正なシンボル (トークン) です。 */
		INVALID_SYMBOL,
		/** 不正な識別子です。 */
		INVALID_IDENTIFIER,
		/** 不正な文字です。 */
		INVALID_CHARACTER,
		/** 不正な定数です */
		INVALID_CONSTANT,
		/** 注釈が入力ファイルの最後まで終わりません。 */
		UNTERMINATED_COMMENT,
		/** 空の文字列です。 */
		NULL_STRING,
		/** 不正な文字列です。 */
		INVALID_STRING,
		/** 予期せぬエラーが発生しました。 */
		ERROR_UNEXPECTED
	}
	
	/**
	 * pas 字句解析中に発生したエラーの内容を標準エラー出力に表示します。
	 * @param errorType
	 * エラーの種類を表す {@link Error Error} 列挙型
	 */
	public void printErrorMessage(Error errorType) {
		String errorMessage = "Line " + lineNum + ": error: ";
		
		switch(errorType) {
		case INVALID_SYMBOL:
			/*MUST UNREACHABLE*/
			errorMessage += "Invalid symbol \"" + string + "\"";
			break;
		case INVALID_IDENTIFIER:
			/*NOUSE*/
			errorMessage += "Invalid identifier \"" + string + "\"";
			break;
		case INVALID_CHARACTER:
			errorMessage += "Invalid character \'" + (char)c + "\'";
			break;
		case INVALID_CONSTANT:
			errorMessage += "Invalid constant \"" + string + "\"";
			break;
		case UNTERMINATED_COMMENT:
			errorMessage += "Unterminated { comment";
			break;
		case NULL_STRING:
			errorMessage += "Null string";
			break;
		case INVALID_STRING:
			errorMessage += "Unterminated string in single line";
			break;
		case ERROR_UNEXPECTED:
		default:
			/*NOUSE*/
			errorMessage += "Unexpected error occurred.";
			break;
		}
		
		System.err.println(errorMessage);
	}
}
